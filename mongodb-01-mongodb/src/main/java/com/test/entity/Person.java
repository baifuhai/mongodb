package com.test.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Person {
	
	String name;
	int age;
	Map<String, Object> interests;
	
	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + ", interests=" + interests + "]";
	}
	
}
