package com.noxception.midisense.interpreter.repository;

import com.noxception.midisense.interpreter.broker.JSONUtils;
import com.noxception.midisense.interpreter.parser.Score;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/** Class that represents an entity equivalent of the {@link Score} class
 * that can be saved as a record in a CRUD repository, with one or more additional methods for referring to the a file designator
 *
 * @author Adrian Rae
 * @author Claudio Teixeira
 * @author Hendro Smit
 * @author Mbuso Shakoane
 * @author Rearabetswe Maeko
 * @since 1.0.0
 */

@Entity
public class ScoreEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    private String fileDesignator;

    @ElementCollection()
    private final List<byte[]> scoreEncoding = new ArrayList<>();

    @ElementCollection()
    private final List<byte[]> fileContents = new ArrayList<>();

    private int maxEncodingLength;
    private int compressedEncodingLength;
    
    private int maxContentLength;
    private int compressedContentLength;

    public ScoreEntity() {
        maxEncodingLength = 0;
        compressedEncodingLength = 0;
        maxContentLength = 0;
        compressedContentLength = 0;
    }

    public ScoreEntity(Score score, UUID fileDesignator, byte[] contents){
        this.fileDesignator = fileDesignator.toString();
        encodeScore(score);
        encodeContents(contents);

    }

    public String getFileDesignator() {
        return fileDesignator;
    }

    public void setFileDesignator(String fileDesignator) {
        this.fileDesignator = fileDesignator;
    }

    public void encodeScore(Score score){

        //get the JSON string of the score
        String JSONScore = score.toString();

        //translate to byte stream
        byte[] input = JSONScore.getBytes();

        //compress
        maxEncodingLength = input.length;
        int compressedMax = Math.max(Short.MAX_VALUE, maxEncodingLength);
        byte[] output = new byte[compressedMax];
        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();
        compressedEncodingLength = deflater.deflate(output);
        deflater.end();

        int len = compressedEncodingLength;

        int segmentSize = 255;
        int i = 0;
        int window = len/segmentSize;
        while (i<= window){
            byte[] portion;
            if (i != window){
                portion = Arrays.copyOfRange(output, i * segmentSize, (i + 1) * segmentSize);
            }
            else{
                portion = Arrays.copyOfRange(output, i * segmentSize, len);
            }
            this.scoreEncoding.add(portion);
            i++;
        }
    }
    
    @SneakyThrows
    public Score decodeScore(){

        List<Byte> reconstruct = new ArrayList<>();
        for(byte[] part: scoreEncoding){
            for(byte b: part) reconstruct.add(b);
        }
        byte[] response = new byte[reconstruct.size()];
        for(int i=0; i<reconstruct.size(); i++) response[i] = reconstruct.get(i);

        byte[] responseArray = new byte[maxEncodingLength];
        try{
            //reconstruct from reduced
            Inflater inflater = new Inflater();
            inflater.setInput(response, 0, compressedEncodingLength);
            int resultLength = inflater.inflate(responseArray);
            inflater.end();
        }
        catch (DataFormatException e) {
            responseArray = new byte[]{};
        }

        return JSONUtils.JSONToObject(new String(responseArray),Score.class);
    }

    public void encodeContents(byte[] input){

        //compress
        maxContentLength = input.length;
        int compressedMax = Math.max(Short.MAX_VALUE, maxContentLength);
        byte[] output = new byte[compressedMax];
        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();
        compressedContentLength = deflater.deflate(output);
        deflater.end();

        int len = compressedContentLength;

        int segmentSize = 255;
        int i = 0;
        int window = len/segmentSize;
        while (i<= window){
            byte[] portion;
            if (i != window){
                portion = Arrays.copyOfRange(output, i * segmentSize, (i + 1) * segmentSize);
            }
            else{
                portion = Arrays.copyOfRange(output, i * segmentSize, len);
            }
            this.fileContents.add(portion);
            i++;
        }
    }

    public byte[] decodeContents(){

        List<Byte> reconstruct = new ArrayList<>();
        for(byte[] part: fileContents){
            for(byte b: part) reconstruct.add(b);
        }
        byte[] response = new byte[reconstruct.size()];
        for(int i=0; i<reconstruct.size(); i++) response[i] = reconstruct.get(i);

        byte[] responseArray = new byte[maxContentLength];
        try{
            //reconstruct from reduced
            Inflater inflater = new Inflater();
            inflater.setInput(response, 0, compressedContentLength);
            int resultLength = inflater.inflate(responseArray);
            inflater.end();
        }
        catch (DataFormatException e) {
            responseArray = new byte[]{};
        }

        return responseArray;
    }
    

    //mocked
    public String getKeySignature(){
        return "CMaj";
    }

    public int getTempoIndication(){
        return 120;
    }

    public String getTimeSignature(){
        return "4/4";
    }


}
