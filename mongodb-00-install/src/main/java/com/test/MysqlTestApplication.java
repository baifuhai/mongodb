package com.test;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class MysqlTestApplication {

	private static MysqlTestApplication INSTANCE = null;

	private JFrame frame;
	private JPanel panelContainer;

	private JTextField textFieldIp;
	private JTextField textFieldPort;
	private JTextField textFieldUsername;
	private JTextField textFieldPassword;
	private JTextField textFieldDataCount;
	private JTextField textFieldThreadCount;

	private JButton buttonWrite1;
	private JButton buttonWrite2;
	private JButton buttonWrite3;
	private JButton buttonStop;
	private JLabel labelProgressInfo;

	private JTextPane textPane;

	private boolean stop = false;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

	private ExecutorService executorService = Executors.newFixedThreadPool(50);

	public static MysqlTestApplication getInstance(boolean singleton) {
		if (singleton) {
			if (INSTANCE == null) {
				synchronized (MysqlTestApplication.class) {
					if (INSTANCE == null) {
						INSTANCE = new MysqlTestApplication(true);
					}
				}
			}
			return INSTANCE;
		} else {
			return new MysqlTestApplication(false);
		}
	}

	private MysqlTestApplication(boolean singleton) {
		// Panel Container
		panelContainer = new JPanel();
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField ip
			panel.add(new JLabel("ip"));
			textFieldIp = new JTextField(15);
			textFieldIp.setText("192.168.1.20");
			panel.add(textFieldIp);

			// TextField port
			panel.add(new JLabel("port"));
			textFieldPort = new JTextField(15);
			textFieldPort.setText("3001");
			panel.add(textFieldPort);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField username
			panel.add(new JLabel("username"));
			textFieldUsername = new JTextField(15);
			textFieldUsername.setText("root");
			panel.add(textFieldUsername);

			// TextField password
			panel.add(new JLabel("password"));
			textFieldPassword = new JTextField(15);
			textFieldPassword.setText("root");
			panel.add(textFieldPassword);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField dataCount
			panel.add(new JLabel("dataCount"));
			textFieldDataCount = new JTextField(15);
			textFieldDataCount.setText("500000");
			panel.add(textFieldDataCount);

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

			// Button write1
			buttonWrite1 = new JButton("测试写入1");
			buttonWrite1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						buttonWrite1.setEnabled(false);
						new Thread(() -> {
							try {
								xx(1);
								JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e) {
								log.error(e.getMessage(), e);
								JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
							} finally {
								buttonWrite1.setEnabled(true);
							}
						}).start();
					}
				}
			});
			panel.add(buttonWrite1);

			// Button write2
			buttonWrite2 = new JButton("测试写入2");
			buttonWrite2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						buttonWrite2.setEnabled(false);
						new Thread(() -> {
							try {
								xx(2);
								JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e) {
								log.error(e.getMessage(), e);
								JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
							} finally {
								buttonWrite2.setEnabled(true);
							}
						}).start();
					}
				}
			});
			panel.add(buttonWrite2);

			// Button write3
			buttonWrite3 = new JButton("测试写入3");
			buttonWrite3.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.YES_OPTION) {
						buttonWrite3.setEnabled(false);
						new Thread(() -> {
							try {
								xx(3);
								JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e) {
								log.error(e.getMessage(), e);
								JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
							} finally {
								buttonWrite3.setEnabled(true);
							}
						}).start();
					}
				}
			});
			panel.add(buttonWrite3);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button stop
			buttonStop = new JButton("停止写入");
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

			// Label progressInfo
//			labelProgressInfo = new JLabel("");
//			panel.add(labelProgressInfo);

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
		frame = new JFrame("mysql测试工具");
		frame.setSize(600, 500);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(panelContainer);
		frame.setDefaultCloseOperation(singleton ? WindowConstants.HIDE_ON_CLOSE : WindowConstants.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(false);
	}

	public void show() {
		frame.setVisible(true);
	}

	private void xx(int mode) throws Exception {
		String ip = textFieldIp.getText();
		String port = textFieldPort.getText();
		String username = textFieldUsername.getText();
		String password = textFieldPassword.getText();
		Integer dataCount = Integer.parseInt(textFieldDataCount.getText());
		Integer threadCount = Integer.parseInt(textFieldThreadCount.getText());

		String url = String.format("jdbc:mysql://%s:%s/?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=false", ip, port);

		Connection conn = null;
		Statement stmt = null;

		try {
			conn = DriverManager.getConnection(url, username, password);
			conn.setAutoCommit(false);

			stmt = conn.createStatement();

			// databaseName
			String databaseName = "test_" + LocalDateTime.now().format(dateTimeFormatter) + "_" + UUID.randomUUID().toString().replace("-", "");

			// create database
			{
				String sql = "create database " + databaseName;
				log.info("{}", sql);
				stmt.execute(sql);
			}

			// create table
			{
				String sql = "" +
						"CREATE TABLE `" + databaseName + "`.`t_user` (" +
						"  `id` int(11) NOT NULL AUTO_INCREMENT," +
						"  `a` varchar(255) DEFAULT NULL," +
						"  `b` varchar(255) DEFAULT NULL," +
						"  `c` varchar(255) DEFAULT NULL," +
						"  `d` varchar(255) DEFAULT NULL," +
						"  `e` varchar(255) DEFAULT NULL," +
						"  `f` double DEFAULT NULL," +
						"  `g` varchar(255) DEFAULT NULL," +
						"  `h` varchar(255) DEFAULT NULL," +
						"  `i` varchar(255) DEFAULT NULL," +
						"  `j` bit(1) DEFAULT NULL," +
						"  `k` varchar(255) DEFAULT NULL," +
						"  `l` varchar(255) DEFAULT NULL," +
						"  `m` varchar(255) DEFAULT NULL," +
						"  `n` varchar(255) DEFAULT NULL," +
						"  `o` varchar(255) DEFAULT NULL," +
						"  `p` varchar(255) DEFAULT NULL," +
						"  `q` bit(1) DEFAULT NULL," +
						"  `r` varchar(255) DEFAULT NULL," +
						"  PRIMARY KEY (`id`)" +
						")";
				log.info("{}", sql);
				stmt.execute(sql);
			}

			// insert data
			{
				String sql = "" +
						"INSERT INTO `" + databaseName + "`.`t_user` (`a`,`b`,`c`,`d`,`e`,`f`,`g`,`h`,`i`,`j`,`k`,`l`,`m`,`n`,`o`,`p`,`q`,`r`)" +
						"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				// 单线程
				if (mode == 1) {
					List<String> messageList = new ArrayList<>();
					messageList.add("");

					insertData(conn, sql, dataCount, messageList, 0);

					conn.commit();
				}

				// 多线程，一个连接
				else if (mode == 2) {
					Connection _conn = conn;

					List<String> messageList = new ArrayList<>();

					List<Callable<Long>> callableList = new ArrayList<>();

					List<Integer> dataCountList = splitCount(dataCount, threadCount);
					for (int i = 0; i < dataCountList.size(); i++) {
						int index = i;
						Integer _dataCount = dataCountList.get(i);

						messageList.add("");

						callableList.add(() -> insertData(_conn, sql, _dataCount, messageList, index));
					}

					executorService.invokeAll(callableList);

					conn.commit();
				}

				// 多线程，多个连接
				else if (mode == 3) {
					List<Connection> connList = new ArrayList<>();
					try {
						List<String> messageList = new ArrayList<>();

						List<Callable<Long>> callableList = new ArrayList<>();

						List<Integer> dataCountList = splitCount(dataCount, threadCount);
						for (int i = 0; i < dataCountList.size(); i++) {
							int index = i;
							Integer _dataCount = dataCountList.get(i);

							Connection _conn = DriverManager.getConnection(url, username, password);
							_conn.setAutoCommit(false);
							connList.add(_conn);

							messageList.add("");

							callableList.add(() -> insertData(_conn, sql, _dataCount, messageList, index));
						}

						executorService.invokeAll(callableList);

						for (Connection _conn : connList) {
							_conn.commit();
						}
					} finally {
						for (Connection _conn : connList) {
							if (_conn != null) {
								try {
									_conn.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}

			// drop database
			{
				String sql = "drop database " + databaseName;
				log.info("{}", sql);
				stmt.execute(sql);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private long insertData(Connection conn, String sql, int count, List<String> messageList, int index) throws Exception {
		long begin = System.currentTimeMillis();

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);

			stop = false;

			for (int i = 1; i <= count; i++) {
				pstmt.setString(1, "110000000000000000");
				pstmt.setString(2, "某某某某某某某某某某某某");
				pstmt.setString(3, "某某某某某某某某某某某某");
				pstmt.setString(4, "120000000000000000");
				pstmt.setString(5, "某某某某某某某某某某某某");
				pstmt.setDouble(6, 0.0);
				pstmt.setString(7, "130000000000000000");
				pstmt.setString(8, "某某某某某某某某某某某某");
				pstmt.setString(9, "某某某某某某某某某某某某");
				pstmt.setInt(10, 1);
				pstmt.setString(11, "140000000000000000,150000000000000000");
				pstmt.setString(12, "某某某某某某某某某某某某,某某某某某某某某某某某某");
				pstmt.setString(13, "[true,true]");
				pstmt.setString(14, "160000000000000000,170000000000000000");
				pstmt.setString(15, "某某某某某某某某某某某某,某某某某某某某某某某某某");
				pstmt.setString(16, "[false,false]");
				pstmt.setInt(17, 1);
				pstmt.setString(18, "180000000000000000");
				pstmt.addBatch();

				if (i % 5000 == 0 || i == count) {
					pstmt.executeBatch();
					log.info("pstmt.executeBatch({}/{})", i, count);
				}

				if (i % 1000 == 0 || i == count) {
					long end = System.currentTimeMillis();
					double percent = i * 100.0 / count;
					double seconds = (end - begin) / 1000.0;
					messageList.set(index, String.format("(%d/%d) %.2f%% %.2fs", i, count, percent, seconds));
					textPane.setText(messageList.stream().collect(Collectors.joining("\n")));
				}

				if (stop) {
					break;
				}
			}

			long end = System.currentTimeMillis();
			long seconds = (end - begin) / 1000;

			return seconds;
		} catch (Exception e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private List<Integer> splitCount(int count, int resultListSize) {
		if (count <= 0) {
			throw new IllegalArgumentException("count should be bigger than 0");
		}
		if (resultListSize <= 0) {
			throw new IllegalArgumentException("resultListSize should be bigger than 0");
		}
		int a = count / resultListSize;
		int b = count % resultListSize;
		List<Integer> resultList = new ArrayList<>();
		for (int i = 0; i < resultListSize; i++) {
			if (i == 0) {
				resultList.add(a + b);
			} else {
				resultList.add(a);
			}
		}
		return resultList;
	}

}
