package com.noxception.midisense.interpreter.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class NoteEntity {
    @Id
    @GeneratedValue
    private UUID noteID;
    private String step;
    private byte octave;
    private boolean isRest;
    private boolean isPercussive;
    private byte onVelocity;
    private byte offVelocity;

    public NoteEntity() {
    }

    public void setStep(String step) {
        this.step = step;
    }

    public void setOctave(byte octave) {
        this.octave = octave;
    }

    public void setRest(boolean rest) {
        isRest = rest;
    }

    public void setPercussive(boolean percussive) {
        isPercussive = percussive;
    }

    public void setOnVelocity(byte onVelocity) {
        this.onVelocity = onVelocity;
    }

    public void setOffVelocity(byte offVelocity) {
        this.offVelocity = offVelocity;
    }

    public UUID getNoteID() {
        return noteID;
    }

    public String getStep() {
        return step;
    }

    public byte getOctave() {
        return octave;
    }

    public boolean isRest() {
        return isRest;
    }

    public boolean isPercussive() {
        return isPercussive;
    }

    public byte getOnVelocity() {
        return onVelocity;
    }

    public byte getOffVelocity() {
        return offVelocity;
    }
}
