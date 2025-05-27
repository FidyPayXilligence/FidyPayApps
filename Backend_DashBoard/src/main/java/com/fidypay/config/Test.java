package com.fidypay.config;

import java.util.ArrayList;
import java.util.List;

import com.fidypay.encryption.Encryption;

public class Test {

	public static void main(String[] args) {
	

		  List<String> subList = new ArrayList<String>();
	        subList.add("Carrot");
	        subList.add("Potato");
	        subList.add("Cauliflower");
	        subList.add("LadyFinger");
	        subList.add("Tomato");
	        System.out.println("------------Vegetable List--------------");
	        subList.forEach(sub -> System.out.println(sub));
		
		
	        System.out.println(Encryption.encString("1"));
	        System.out.println(Encryption.encString("manan.dixit@fidypay.com"));

	}

}
