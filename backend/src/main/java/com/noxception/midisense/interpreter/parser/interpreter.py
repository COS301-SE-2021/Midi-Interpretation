import sys

import mido

file = str(sys.argv[1])
target_channel = int(sys.argv[2])

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

mid = mido.MidiFile(file)
tpb = mid.ticks_per_beat
final_sequence = {}
channel = None
program = None
found = False

for i, track in enumerate(mid.tracks, start=0):
    clock = 0
    sequence = {}
    for msg in track:
        clock += msg.time
        if msg.type == 'program_change' and (program is None and channel is None):
            if msg.channel == target_channel:
                found = True
                program = msg.program
                channel = msg.channel
            else:
                break

        elif msg.type == "note_on":
            note = {"value": msg.note, "on_velocity": msg.velocity, "off_velocity": -1, "duration": -1}
            if sequence.get(clock) is None:
                sequence[clock] = [note]
            else:
                if msg.note not in [k['value'] for k in sequence.get(clock)]:
                    sequence[clock].append(note)

        elif msg.type == "note_off":
            # go back to the same valued note and adjust duration
            timing_index = [s for s in sequence.keys()]
            timing_index.sort(reverse=True)
            for t in timing_index:
                note = msg.note
                pitches = [k['value'] for k in sequence.get(t)]
                if note in pitches:
                    pos = pitches.index(note)
                    sequence[t][pos]['off_velocity'] = msg.velocity
                    sequence[t][pos]['duration'] = (clock - t)
                    break

    if found:
        final_sequence = [{"tick": s, "notes": sequence.get(s)} for s in sequence.keys()]
        break

instrument = "Drumset" if channel == 9 else instruments[program]
result = {"channel": channel, "instrument": instrument, 'ticks_per_beat': tpb, "track": final_sequence}
print(result.__str__().replace('\'', '\"'))
