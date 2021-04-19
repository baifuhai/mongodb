package com.test;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class CpuTestApplication {

	private static CpuTestApplication INSTANCE = null;

	private JFrame frame;
	private JPanel panelContainer;

	private JTextField textFieldDataCount;
	private JTextField textFieldSortCount;
	private JTextField textFieldThreadCount;

	private JButton buttonStart;
	private JButton buttonStop;

	private JTextPane textPane;

	private boolean stop = false;

	private ExecutorService executorService = Executors.newFixedThreadPool(50);

	public static CpuTestApplication getInstance(boolean singleton) {
		if (singleton) {
			if (INSTANCE == null) {
				synchronized (CpuTestApplication.class) {
					if (INSTANCE == null) {
						INSTANCE = new CpuTestApplication(true);
					}
				}
			}
			return INSTANCE;
		} else {
			return new CpuTestApplication(false);
		}
	}

	private CpuTestApplication(boolean singleton) {
		// Panel Container
		panelContainer = new JPanel();
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField dataCount
			panel.add(new JLabel("dataCount"));
			textFieldDataCount = new JTextField(15);
			textFieldDataCount.setText("50000");
			panel.add(textFieldDataCount);

			// TextField sortCount
			panel.add(new JLabel("sortCount"));
			textFieldSortCount = new JTextField(15);
			textFieldSortCount.setText("300");
			panel.add(textFieldSortCount);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField threadCount
			panel.add(new JLabel("threadCount"));
			textFieldThreadCount = new JTextField(15);
			textFieldThreadCount.setText("" + Runtime.getRuntime().availableProcessors());
			panel.add(textFieldThreadCount);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button start
			buttonStart = new JButton("开始测试");
			buttonStart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						buttonStart.setEnabled(false);
						new Thread(() -> {
							try {
								xx();
								JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e) {
								log.error(e.getMessage(), e);
								JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
							} finally {
								buttonStart.setEnabled(true);
							}
						}).start();
					}
				}
			});
			panel.add(buttonStart);

			// Button stop
			buttonStop = new JButton("停止测试");
			buttonStop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						stop = true;
					}
				}
			});
			panel.add(buttonStop);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// TextPane
			textPane = new JTextPane();
			textPane.setEditable(false);

			// JScrollPane
			JScrollPane scrollPane = new JScrollPane(textPane);
			scrollPane.setPreferredSize(new Dimension(0, 300));

			// add to panelContainer
			panelContainer.add(scrollPane);
		}

		// Frame
		frame = new JFrame("cpu测试工具");
		frame.setSize(600, 410);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(panelContainer);
		frame.setDefaultCloseOperation(singleton ? WindowConstants.HIDE_ON_CLOSE : WindowConstants.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(false);
	}

	public void show() {
		frame.setVisible(true);
	}

	private void xx() throws Exception {
		Integer dataCount = Integer.parseInt(textFieldDataCount.getText());
		Integer sortCount = Integer.parseInt(textFieldSortCount.getText());
		Integer threadCount = Integer.parseInt(textFieldThreadCount.getText());

		List<String> messageList = new ArrayList<>();

		List<Callable<Long>> callableList = new ArrayList<>();

		for (int i = 0; i < threadCount; i++) {
			int index = i;

			messageList.add("");

			callableList.add(() -> {
				long begin = System.currentTimeMillis();

				List<Integer> list = new ArrayList<>(dataCount);
				for (int j = 0; j < dataCount; j++) {
					list.add(j);
				}

				stop = false;

				for (int j = 1; j <= sortCount; j++) {
					Collections.sort(list);
					Collections.shuffle(list);

					if (j % 1 == 0 || j == sortCount) {
						long end = System.currentTimeMillis();
						double percent = j * 100.0 / sortCount;
						double seconds = (end - begin) / 1000.0;
						messageList.set(index, String.format("(%d/%d) %.2f%% %.2fs", j, sortCount, percent, seconds));
						if (index == 0) {
							textPane.setText(messageList.stream().collect(Collectors.joining("\n")));
						}
						if (j == sortCount) {
							synchronized (textPane) {
								textPane.setText(messageList.stream().collect(Collectors.joining("\n")));
							}
						}
					}

					if (stop) {
						break;
					}
				}

				return null;
			});
		}

		executorService.invokeAll(callableList);
	}

}
