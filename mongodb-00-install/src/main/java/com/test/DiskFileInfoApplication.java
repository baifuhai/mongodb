package com.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.test.domain.DirInfo;
import com.test.domain.FileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DiskFileInfoApplication {

	private static DiskFileInfoApplication INSTANCE = null;

	private JFrame frame;
	private JPanel panelContainer;

	private JLabel lebel;

	private Map<JToggleButton, File> buttonRootMap = new LinkedHashMap<>();

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

	public static DiskFileInfoApplication getInstance(boolean singleton) {
		if (singleton) {
			if (INSTANCE == null) {
				synchronized (DiskFileInfoApplication.class) {
					if (INSTANCE == null) {
						INSTANCE = new DiskFileInfoApplication(true);
					}
				}
			}
			return INSTANCE;
		} else {
			return new DiskFileInfoApplication(false);
		}
	}

	private DiskFileInfoApplication(boolean singleton) {
		// Panel Container
		panelContainer = new JPanel();
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// JToggleButton
			File[] roots = File.listRoots();
			for (File root : roots) {
				String format = String.format("%s (%.2f%%)", root.getPath(), 100.0 - root.getUsableSpace() * 100.0 / root.getTotalSpace());

				JToggleButton toggleButton = new JToggleButton(format);
				if (!root.getPath().startsWith("C:")) {
					toggleButton.setSelected(true);
				}
				panel.add(toggleButton);

				buttonRootMap.put(toggleButton, root);
			}

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Path
			String path = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop";

			// FileChooser
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setSelectedFile(new File(path));

			// TextField
			JTextField textField = new JTextField(33);
			textField.setEditable(false);
			textField.setText(path);
			panel.add(textField);

			// Button
			JButton button = new JButton("选择导出目录");
			button.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					int returnVal = fileChooser.showOpenDialog(panelContainer);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
					}
				}
			});
			panel.add(button);

			// Button Export
			JButton buttonExport = new JButton("导出");
			buttonExport.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event) {
					File exportDir = fileChooser.getSelectedFile();
					if (exportDir == null) {
						JOptionPane.showMessageDialog(panelContainer, "请选择导出目录", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						buttonExport.setEnabled(false);
						new Thread(() -> {
							try {
								export(exportDir);
								JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e) {
								log.error(e.getMessage(), e);
								JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
							} finally {
								buttonExport.setEnabled(true);
							}
						}).start();
					}
				}
			});
			panel.add(buttonExport);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Label
			lebel = new JLabel();
			panel.add(lebel);

			// add to panelContainer
			panelContainer.add(panel);
		}

		// Frame
		frame = new JFrame("硬盘文件信息获取工具");
		frame.setSize(600, 300);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(panelContainer);
		frame.setDefaultCloseOperation(singleton ? WindowConstants.HIDE_ON_CLOSE : WindowConstants.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(false);
	}

	public void show() {
		frame.setVisible(true);
	}

	private void export(File exportDir) throws Exception {
		String exportFileName = "diskFileInfo-" + LocalDateTime.now().format(dateTimeFormatter) + ".txt";
		File exportFile = new File(exportDir, "diskFileInfo/" + exportFileName);
		if (!exportFile.getParentFile().exists()) {
			exportFile.getParentFile().mkdirs();
		}

		List<DirInfo> dirInfoList = new ArrayList<>();
		/*
		File[] roots = File.listRoots();
		if (roots != null) {
			for (File root : roots) {
				DirInfo dirInfo = recurse(root);
				dirInfoList.add(dirInfo);
			}
		}
		*/
		for (Map.Entry<JToggleButton, File> entry : buttonRootMap.entrySet()) {
			JToggleButton button = entry.getKey();
			File root = entry.getValue();
			if (button.isSelected()) {
				DirInfo dirInfo = recurse(root);
				dirInfoList.add(dirInfo);
			}
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(exportFile);
			fw.write(JSONObject.toJSONString(dirInfoList, SerializerFeature.PrettyFormat));
			fw.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		lebel.setText("");
	}

	private DirInfo recurse(File dir) {
		DirInfo dirInfo = new DirInfo(dir);

		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				lebel.setText(file.getAbsolutePath());
				if (file.isFile()) {
					FileInfo fileInfo = new FileInfo(file);
					dirInfo.addFileInfo(fileInfo);
				} else if (file.isDirectory()) {
					DirInfo dirInfo2 = recurse(file);
					dirInfo.addDirInfo(dirInfo2);
				}
			}
		}

		return dirInfo;
	}

}
