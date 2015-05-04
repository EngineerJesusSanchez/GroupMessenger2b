package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;

public class MulticastMessage implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int seqNumber;
    private String message;
    private int[] vector;

    public MulticastMessage(int seqNumber, String message) {
        super();
        this.seqNumber = seqNumber;
        this.message = message;
    }

    public MulticastMessage(int seqNumber, String message, int[] vector) {
        super();
        this.seqNumber = seqNumber;
        this.message = message;
        this.vector = vector;
    }

    public MulticastMessage(){

    }

    /**
     * @return the seqNumber
     */
    public int getSeqNumber() {
        return seqNumber;
    }
    /**
     * @param seqNumber the seqNumber to set
     */
    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public int[] getVector() {
        return vector;
    }

    public void setVector(int[] vector) {
        this.vector = vector;
    }

}

Status API Training Shop Blog About

