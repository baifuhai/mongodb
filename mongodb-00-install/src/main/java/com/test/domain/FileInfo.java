package com.test.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.File;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileInfo {

	String name;

	String path;

	Long length;

	public FileInfo(File file) {
		this.name = file.getName();
		this.path = file.getPath().replace("\\", "/");
		this.length = file.length();
	}

}
