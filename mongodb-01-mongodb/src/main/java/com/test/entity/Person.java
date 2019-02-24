package com.test.entity;

import java.util.Map;

public class Person {
	
	private String name;
	private int age;
	private Map<String, Object> interests;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Map<String, Object> getInterests() {
		return interests;
	}
	public void setInterests(Map<String, Object> interests) {
		this.interests = interests;
	}
	
	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + ", interests=" + interests + "]";
	}
	
}
