package com.test.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DirInfo extends FileInfo {

	List<DirInfo> dirInfoList;

	List<FileInfo> fileInfoList;

	public DirInfo(File dir) {
		super(dir);
		this.dirInfoList = new ArrayList<>();
		this.fileInfoList = new ArrayList<>();
	}

	public void addDirInfo(DirInfo dirInfo) {
		this.dirInfoList.add(dirInfo);
	}

	public void addFileInfo(FileInfo fileInfo) {
		this.fileInfoList.add(fileInfo);
	}

}
