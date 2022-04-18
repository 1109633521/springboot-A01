package com.example.springboota01;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import ws.schild.jave.*;


import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class txtTest {
    public static void main(String[] args) {

    }

    @Test
    public void testVideotoAudio() {
        File file = new File("C:/Environment/file_a01/video/641ac34048da43fab399069a78bef172.mp4");
        try {
            // 设置输出流的格式
            File target = new File("C:/Environment/file_a01/video/target.mp4");
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");//音频编码格式
            //audio.setBitRate(new Integer(56000));//设置比特率，比特率越大，转出来的音频越大（默认是128000，最好默认就行，有特殊要求再设置）
            audio.setChannels(new Integer(1));
            audio.setSamplingRate(new Integer(22050));
            VideoAttributes video = new VideoAttributes();
            video.setCodec("libx264");//视屏编码格式
            //video.setBitRate(new Integer(56000));//设置比特率，比特率越大，转出来的视频越大（默认是128000,最好默认就行，有特殊要求再设置）
            video.setFrameRate(new Integer(15));//数值设置小了，视屏会卡顿
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("mp4");
            attrs.setAudioAttributes(audio);
            attrs.setVideoAttributes(video);
            Encoder encoder = new Encoder();
            MultimediaObject multimediaObject = new MultimediaObject(file);
            try {
                encoder.encode(multimediaObject, target, attrs);
                System.out.println("转换成功！");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InputFormatException e) {
                e.printStackTrace();
            } catch (EncoderException e) {
                e.printStackTrace();
            }

        } finally {

        }
    }

    @Test
    public void hahaha(){
        File file = new File("C:/Environment/file_a01/video/641ac34048da43fab399069a78bef172.mp4");

    }

    @Test
    public void stringTest(){
        String number = "id=0";
        System.out.println(number.substring(0,2));
        if(number.substring(0,2)=="id"){
            System.out.println("yes");
        }
    }
}
