package com.skplanet.tas.hadoop.review.io;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.apache.hadoop.io.IntWritable;

public class Deserialization {

   public IntWritable deserialize(byte[]byteArray) throws Exception{
   
      //Instantiating the IntWritable class
      IntWritable intwritable =new IntWritable();
      
      //Instantiating ByteArrayInputStream object
      ByteArrayInputStream InputStream = new ByteArrayInputStream(byteArray);
      
      //Instantiating DataInputStream object
      DataInputStream datainputstream=new DataInputStream(InputStream);
      
      //deserializing the data in DataInputStream
      intwritable.readFields(datainputstream);
      
      //printing the serialized data
//      System.out.println((intwritable).get());
      return intwritable;
   }
   
   public static void main(String args[]) throws Exception {
      Deserialization dese = new Deserialization();
//      dese.deserialize(new Serialization().serialize());
   }
}