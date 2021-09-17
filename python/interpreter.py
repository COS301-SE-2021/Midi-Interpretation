import os

import mido
from flask import Flask, request
import uuid
import json

# ======================================================
# CONFIGURATION
# ======================================================

app = Flask(__name__)
instruments = ["Acoustic Grand Piano", "Bright Acoustic Piano", "Electric Grand Piano", "Honky-tonk Piano",
               "Electric Piano 1", "Electric Piano 2", "Harpsichord", "Clavi", "Celesta", "Glockenspiel", "Music Box",
               "Vibraphone", "Marimba", "Xylophone", "Tubular Bells", "Dulcimer", "Drawbar Organ", "Percussive Organ",
               "Rock Organ", "Church Organ", "Reed Organ", "Accordion", "Harmonica", "Tango Accordion",
               "Acoustic Guitar (nylon)", "Acoustic Guitar (steel)", "Electric Guitar (jazz)",
               "Electric Guitar (clean)", "Electric Guitar (muted)", "Overdriven Guitar", "Distortion Guitar",
               "Guitar harmonics", "Acoustic Bass", "Electric Bass (finger)", "Electric Bass (pick)", "Fretless Bass",
               "Slap Bass 1", "Slap Bass 2", "Synth Bass 1", "Synth Bass 2", "Violin", "Viola", "Cello", "Contrabass",
               "Tremolo Strings", "Pizzicato Strings", "Orchestral Harp", "Timpani", "String Ensemble 1",
               "String Ensemble 2", "SynthStrings 1", "SynthStrings 2", "Choir Aahs", "Voice Oohs", "Synth Voice",
               "Orchestra Hit", "Trumpet", "Trombone", "Tuba", "Muted Trumpet", "French Horn", "Brass Section",
               "SynthBrass 1", "SynthBrass 2", "Soprano Sax", "Alto Sax", "Tenor Sax", "Baritone Sax", "Oboe",
               "English Horn", "Bassoon", "Clarinet", "Piccolo", "Flute", "Recorder", "Pan Flute", "Blown Bottle",
               "Shakuhachi", "Whistle", "Ocarina", "Lead 1 (square)", "Lead 2 (sawtooth)", "Lead 3 (calliope)",
               "Lead 4 (chiff)", "Lead 5 (charang)", "Lead 6 (voice)", "Lead 7 (fifths)", "Lead 8 (bass + lead)",
               "Pad 1 (new age)", "Pad 2 (warm)", "Pad 3 (polysynth)", "Pad 4 (choir)", "Pad 5 (bowed)",
               "Pad 6 (metallic)", "Pad 7 (halo)", "Pad 8 (sweep)", "FX 1 (rain)", "FX 2 (soundtrack)",
               "FX 3 (crystal)", "FX 4 (atmosphere)", "FX 5 (brightness)", "FX 6 (goblins)", "FX 7 (echoes)",
               "FX 8 (sci-fi)", "Sitar", "Banjo", "Shamisen", "Koto", "Kalimba", "Bag pipe", "Fiddle", "Shanai",
               "Tinkle Bell", "Agogo", "Steel Drums", "Woodblock", "Taiko Drum", "Melodic Tom", "Synth Drum",
               "Reverse Cymbal", "Guitar Fret Noise", "Breath Noise", "Seashore", "Bird Tweet", "Telephone Ring",
               "Helicopter", "Applause", "Gunshot"]


# ======================================================
# ROUTING
# ======================================================

@app.route('/interpret', methods=['POST'])
def interpret_sequence():
    body_data = request.json
    interpretation = interpret_messages(body_data)
    if interpretation is not None:
        return interpretation, 200
    else:
        return "Interpretation failed. Your file is most likely corrupted.", 400


def interpret_messages(byte_stream):
    # create a new midi file with the byte stream passed in
    temp_name = str(uuid.uuid4()) + ".mid"
    temp_file = open(temp_name, "wb")
    temp_file.write(bytearray(byte_stream))
    temp_file.close()

    interpretation_dictionary = {}
    try:
        parser = mido.MidiFile(temp_name)
        interpretation_dictionary = parse(parser)
    except Exception as e:
        interpretation_dictionary = None
    finally:
        # delete the temporary file
        if os.path.exists(temp_name):
            os.remove(temp_name)

    return interpretation_dictionary


def parse(parser):
    # have assignment of current channel and assigned program
    channel = None
    program = {}
    sequence = {}
    time_signatures = {}
    key_signatures = {}
    tempos = {}
    tpb = parser.ticks_per_beat
    for i, track in enumerate(parser.tracks, start=0):
        clock = 0

        for msg in track:
            clock += msg.time

            if msg.type == 'program_change':
                channel = msg.channel
                program[channel] = msg.program
                if sequence.get(channel) is None:
                    sequence[channel] = {}

            elif msg.type == 'key_signature' and key_signatures.get(clock) is None:
                key_signatures[clock] = msg.key

            elif msg.type == 'time_signature' and time_signatures.get(clock) is None:
                time_signatures[clock] = {'num_beats': msg.numerator, 'beat_value': msg.denominator}

            elif msg.type == 'set_tempo' and tempos.get(clock) is None:
                tempos[clock] = 60000000 / msg.tempo

            elif msg.type == "note_on" and msg.velocity > 0:
                channel = msg.channel
                if sequence.get(channel) is None:
                    sequence[channel] = {}
                note = {"value": msg.note, "on_velocity": msg.velocity, "off_velocity": None, "duration": None}
                if sequence[channel].get(clock) is None:
                    sequence[channel][clock] = [note]
                else:
                    if msg.note not in [k['value'] for k in sequence[channel].get(clock)]:
                        sequence[channel][clock].append(note)

            elif msg.type == "note_off" or (msg.type == "note_on" and msg.velocity == 0):
                channel = msg.channel
                # go back to the same valued note and adjust duration
                timing_index = [s for s in sequence[channel].keys()]
                timing_index.sort(reverse=True)
                for t in timing_index:
                    note = msg.note
                    pitches = [k['value'] for k in sequence[channel].get(t)]
                    if note in pitches:
                        pos = pitches.index(note)
                        sequence[channel][t][pos]['off_velocity'] = msg.velocity
                        sequence[channel][t][pos]['duration'] = (clock - t)
                        break

    channel_array = []
    for channel in sequence.keys():
        if sequence.get(channel) is not None:

            final_sequence = [{"tick": s, "notes": sequence[channel].get(s)} for s in sequence[channel].keys()]

            instrument = None
            if program is not None and channel is not None:
                instrument = "Drumset" if channel == 9 else instruments[program[channel]]
            else:
                instrument = "Unknown"

            result = {"channel": channel, "instrument": instrument, 'ticks_per_beat': tpb,
                      "track": final_sequence}

            if len(final_sequence) != 0:
                channel_array.append(result)

    key_list = [{'tick': t, "key": key_signatures.get(t)} for t in key_signatures.keys()]
    time_list = [{'tick': t, "time": time_signatures.get(t)} for t in time_signatures.keys()]
    tempo_list = [{'tick': t, "tempo": tempos.get(t)} for t in tempos.keys()]
    return {"key_signatures": key_list, "time_signatures": time_list, "tempos": tempo_list,
            "channel_list": channel_array}


# ======================================================
# OPERATION
# ======================================================

if __name__ == '__main__':
    app.run(debug=False)
