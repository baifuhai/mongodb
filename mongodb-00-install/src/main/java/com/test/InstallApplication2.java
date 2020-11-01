package com.test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.test.domain.CommandResult;
import com.test.domain.MongoDatabaseCallback;
import com.test.domain.MongodbParam;
import com.test.util.MongoUtil;
import com.test.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class InstallApplication2 {

	private JFrame frame;

	private JPanel panelContainer;

	private JTextField textFieldMongodbCluster;
	private JButton buttonMongodbCluster;
	private JFileChooser fileChooserMongodbCluster;

	private JTextField textFieldMongodbHome;
	private JButton buttonMongodbHome;
	private JFileChooser fileChooserMongodbHome;

	private JTextField textFieldConfigBindIp;
	private JTextField textFieldConfigIp;
	private JTextField textFieldConfigPort;

	private JTextField textFieldMongosBindIp;
	private JTextField textFieldMongosIp;
	private JTextField textFieldMongosPort;

	private JTextField textFieldShardBindIp;
	private JTextField textFieldShardIp;
	private JTextField textFieldShardPort;

	private JTextField textFieldBeginShardNumber;
	private JTextField textFieldEndShardNumber;

	private JButton buttonRun;

	private JTextPane textPane;

	public void run() {
		// Panel Container
		panelContainer = new JPanel();
		panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField mongodbCluster
			textFieldMongodbCluster = new JTextField(26);
			textFieldMongodbCluster.setEditable(false);
			textFieldMongodbCluster.setText("E:/dev/mongodb-cluster");
			panel.add(textFieldMongodbCluster);

			// Button mongodbCluster
			buttonMongodbCluster = new JButton("选择mongodb-cluster目录");
			buttonMongodbCluster.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int returnVal = fileChooserMongodbCluster.showOpenDialog(panelContainer);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						textFieldMongodbCluster.setText(fileChooserMongodbCluster.getSelectedFile().getAbsolutePath());
					}
				}
			});
			panel.add(buttonMongodbCluster);

			// FileChooser mongodbCluster
			fileChooserMongodbCluster = new JFileChooser();
			fileChooserMongodbCluster.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooserMongodbCluster.setMultiSelectionEnabled(false);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField mongodbHome
			textFieldMongodbHome = new JTextField(26);
			textFieldMongodbHome.setEditable(false);
			textFieldMongodbHome.setText("E:/dev/mongodb-win32-x86_64-2012plus-4.2.7");
			panel.add(textFieldMongodbHome);

			// Button mongodbHome
			buttonMongodbHome = new JButton("选择mongodb-home目录");
			buttonMongodbHome.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int returnVal = fileChooserMongodbHome.showOpenDialog(panelContainer);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						textFieldMongodbHome.setText(fileChooserMongodbHome.getSelectedFile().getAbsolutePath());
					}
				}
			});
			panel.add(buttonMongodbHome);

			// FileChooser mongodbHome
			fileChooserMongodbHome = new JFileChooser();
			fileChooserMongodbHome.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooserMongodbHome.setMultiSelectionEnabled(false);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField configBindIp
			panel.add(new JLabel("configBindIp"));
			textFieldConfigBindIp = new JTextField(15);
			textFieldConfigBindIp.setText("127.0.0.1");
			panel.add(textFieldConfigBindIp);

			// TextField configIp
			panel.add(new JLabel("configIp"));
			textFieldConfigIp = new JTextField(15);
			textFieldConfigIp.setText("127.0.0.1");
			panel.add(textFieldConfigIp);

			// TextField configPort
			panel.add(new JLabel("configPort"));
			textFieldConfigPort = new JTextField(15);
			textFieldConfigPort.setText("26999");
			panel.add(textFieldConfigPort);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField mongosBindIp
			panel.add(new JLabel("mongosBindIp"));
			textFieldMongosBindIp = new JTextField(15);
			textFieldMongosBindIp.setText("127.0.0.1");
			panel.add(textFieldMongosBindIp);

			// TextField mongosIp
			panel.add(new JLabel("mongosIp"));
			textFieldMongosIp = new JTextField(15);
			textFieldMongosIp.setText("127.0.0.1");
			panel.add(textFieldMongosIp);

			// TextField mongosPort
			panel.add(new JLabel("mongosPort"));
			textFieldMongosPort = new JTextField(15);
			textFieldMongosPort.setText("27000");
			panel.add(textFieldMongosPort);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField shardBindIp
			panel.add(new JLabel("shardBindIp"));
			textFieldShardBindIp = new JTextField(15);
			textFieldShardBindIp.setText("127.0.0.1");
			panel.add(textFieldShardBindIp);

			// TextField shardIp
			panel.add(new JLabel("shardIp"));
			textFieldShardIp = new JTextField(15);
			textFieldShardIp.setText("127.0.0.1");
			panel.add(textFieldShardIp);

			// TextField shardPort
			panel.add(new JLabel("shardPort"));
			textFieldShardPort = new JTextField(15);
			textFieldShardPort.setText("27000");
			panel.add(textFieldShardPort);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// TextField beginShardNumber
			panel.add(new JLabel("beginShardNumber"));
			textFieldBeginShardNumber = new JTextField(15);
			textFieldBeginShardNumber.setText("1");
			panel.add(textFieldBeginShardNumber);

			// TextField endShardNumber
			panel.add(new JLabel("endShardNumber"));
			textFieldEndShardNumber = new JTextField(15);
			textFieldEndShardNumber.setText("5");
			panel.add(textFieldEndShardNumber);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// Panel
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));

			// Button run
			buttonRun = new JButton("运行");
			buttonRun.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					int returnVal = JOptionPane.showConfirmDialog(panelContainer, "确定？", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.NO_OPTION) {
						return;
					}

					new Thread(() -> {
						try {
							run(event);
							JOptionPane.showMessageDialog(panelContainer, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							JOptionPane.showMessageDialog(panelContainer, "执行失败：" + e.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
						}
					}).start();
				}
			});
			panel.add(buttonRun);

			// add to panelContainer
			panelContainer.add(panel);
		}

		{
			// TextPane
			textPane = new JTextPane();
//			textPane.setForeground(Color.WHITE);
//			textPane.setBackground(Color.BLACK);
//			textPane.setFont(new Font("宋体", Font.PLAIN, 12));
			textPane.setForeground(new Color(204, 204, 204));
			textPane.setBackground(new Color(12, 12, 12));
			textPane.setFont(new Font("宋体", Font.PLAIN, 16));
			textPane.setEditable(false);

			// JScrollPane
			JScrollPane scrollPane = new JScrollPane(textPane);
			scrollPane.setPreferredSize(new Dimension(0, 700));

			// add to panelContainer
			panelContainer.add(scrollPane);
		}

		// Frame
		frame = new JFrame("mongodb安装工具");
		frame.setSize(900, 900);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(panelContainer);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private void run(ActionEvent event) throws Exception {
		String mongodbCluster = textFieldMongodbCluster.getText();
		String mongodbHome = textFieldMongodbHome.getText();

		String configBindIp = textFieldConfigBindIp.getText();
		String configIp = textFieldConfigIp.getText();
		int configPort = Integer.valueOf(textFieldConfigPort.getText());

		String mongosBindIp = textFieldMongosBindIp.getText();
		String mongosIp = textFieldMongosIp.getText();
		int mongosPort = Integer.valueOf(textFieldMongosPort.getText());

		String shardBindIp = textFieldShardBindIp.getText();
		String shardIp = textFieldShardIp.getText();
		int beginShardPort = Integer.valueOf(textFieldShardPort.getText());

		int beginShardNumber = Integer.valueOf(textFieldBeginShardNumber.getText());
		int endShardNumber = Integer.valueOf(textFieldEndShardNumber.getText());

		// mongos
		{
			String serviceName = "MongoDBMongos";

			stopService(serviceName, textPane);
			deleteService(serviceName, textPane);
		}

		// shard
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			String serviceName = "MongoDBShard" + i;

			stopService(serviceName, textPane);
			deleteService(serviceName, textPane);
		}

		// config
		{
			String serviceName = "MongoDBConfig";

			stopService(serviceName, textPane);
			deleteService(serviceName, textPane);
		}

		// copy dir
		{
			appendTextLn(textPane);
			appendTextLn("copy dir", textPane);

			mongodbCluster = mongodbCluster.replace("\\", "/");
			mongodbHome = mongodbHome.replace("\\", "/");

			File fromDir = new File(this.getClass().getClassLoader().getResource("install/mongodb-cluster").toURI());
//			File fromDir = JarUtil.getResource("install/mongodb-cluster");

			File newDir = new File(mongodbCluster);

			if (newDir.isDirectory()) {
				File[] files = newDir.listFiles();
				if (files != null && files.length > 0) {
					String message = newDir.getAbsolutePath() + "不是空目录，将被清空，是否继续？";
					int returnVal = JOptionPane.showConfirmDialog(panelContainer, message, "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (returnVal == JOptionPane.NO_OPTION) {
						return;
					}
				}
			}

			MongodbParam mongodbParam = new MongodbParam();

			mongodbParam.setMongodbCluster(mongodbCluster);
			mongodbParam.setMongodbHome(mongodbHome);

			mongodbParam.setConfigBindIp(configBindIp);
			mongodbParam.setConfigIp(configIp);
			mongodbParam.setConfigPort(configPort);

			mongodbParam.setMongosBindIp(mongosBindIp);
			mongodbParam.setMongosIp(mongosIp);
			mongodbParam.setMongosPort(mongosPort);

			mongodbParam.setShardBindIp(shardBindIp);
			mongodbParam.setShardIp(shardIp);
			mongodbParam.setBeginShardPort(beginShardPort);

			mongodbParam.setBeginShardNumber(beginShardNumber);
			mongodbParam.setEndShardNumber(endShardNumber);

			MyUtil.generate(fromDir, newDir, mongodbParam);
//			MyUtil.deleteDir(fromDir);
		}

		String mongodPath = mongodbHome + "/bin/mongod.exe";
		String mongosPath = mongodbHome + "/bin/mongos.exe";

		String binPathFormat = "'%s' --service --config '%s'";
		binPathFormat = binPathFormat.replace("'", "\\\"");

		// config
		{
			String serviceName = "MongoDBConfig";
			String configPath = mongodbCluster + "/config/mongo.cfg";
			String binPath = String.format(binPathFormat, mongodPath, configPath);

//			stopService(serviceName, textPane);
//			deleteService(serviceName, textPane);
			createService(serviceName, binPath, textPane);
			startService(serviceName, textPane);

			String replicaSetId = "configReplicaSet";
			replicaSetInit(configIp, configPort, replicaSetId, configIp, configPort, true, textPane, mongodbHome);
		}

		// shard
		for (int i = beginShardNumber; i <= endShardNumber; i++) {
			String serviceName = "MongoDBShard" + i;
			String configPath = mongodbCluster + "/shard/s" + i + "/mongo.cfg";
			String binPath = String.format(binPathFormat, mongodPath, configPath);

//			stopService(serviceName, textPane);
//			deleteService(serviceName, textPane);
			createService(serviceName, binPath, textPane);
			startService(serviceName, textPane);

			String replicaSetId = "shardReplicaSet" + i;
			int shardPort = beginShardPort + i;
			replicaSetInit(shardIp, shardPort, replicaSetId, shardIp, shardPort, false, textPane, mongodbHome);
		}

		// mongos
		{
			String serviceName = "MongoDBMongos";
			String configPath = mongodbCluster + "/mongos/mongo.cfg";
			String binPath = String.format(binPathFormat, mongosPath, configPath);

//			stopService(serviceName, textPane);
//			deleteService(serviceName, textPane);
			createService(serviceName, binPath, textPane);
			startService(serviceName, textPane);

			mongosInit(mongosIp, mongosPort, shardIp, beginShardPort, beginShardNumber, endShardNumber, textPane, mongodbHome);
		}
	}

	private void appendText(CommandResult commandResult, JTextPane textPane) {
		appendText(commandResult.getResult() + commandResult.getErrorMessage(), textPane);
	}

	private void appendTextLn(CommandResult commandResult, JTextPane textPane) {
		appendTextLn(commandResult.getResult() + commandResult.getErrorMessage(), textPane);
	}

	private void appendText(Document document, JTextPane textPane) {
		JsonWriterSettings writerSettings = JsonWriterSettings.builder()
				.indent(true)
				.indentCharacters("\t")
				.build();
		appendText(document.toJson(writerSettings), textPane);
	}

	private void appendTextLn(Document document, JTextPane textPane) {
		JsonWriterSettings writerSettings = JsonWriterSettings.builder()
				.indent(true)
				.indentCharacters("\t")
				.build();
		appendTextLn(document.toJson(writerSettings), textPane);
	}

	private void appendTextRunCommand(Document document, JTextPane textPane) {
		JsonWriterSettings writerSettings = JsonWriterSettings.builder()
				.indent(true)
				.indentCharacters("\t")
				.build();
		appendText("db.runCommand(" + document.toJson(writerSettings) + ")", textPane);
	}

	private void appendTextRunCommandLn(Document document, JTextPane textPane) {
		JsonWriterSettings writerSettings = JsonWriterSettings.builder()
				.indent(true)
				.indentCharacters("\t")
				.build();
		appendTextLn("db.runCommand(" + document.toJson(writerSettings) + ")", textPane);
	}

	private void appendText(Exception exception, JTextPane textPane) {
		appendText(exception.getMessage(), textPane);
	}

	private void appendTextLn(Exception exception, JTextPane textPane) {
		appendTextLn(exception.getMessage(), textPane);
	}

	private void appendText(String text, JTextPane textPane) {
		textPane.setText(textPane.getText() + text);
	}

	private void appendTextLn(String text, JTextPane textPane) {
		textPane.setText(textPane.getText() + text + System.lineSeparator());
	}

	private void appendTextLn(JTextPane textPane) {
		textPane.setText(textPane.getText() + System.lineSeparator());
	}

	private void execute(String command, JTextPane textPane) throws Exception {
		CommandResult commandResult = MyUtil.execute(command);
		appendText(commandResult, textPane);
	}

	private void stopService(String serviceName, JTextPane textPane) throws Exception {
		CommandResult commandResult = MyUtil.stopService(serviceName);
		appendText(commandResult, textPane);
	}

	private void startService(String serviceName, JTextPane textPane) throws Exception {
		CommandResult commandResult = MyUtil.startService(serviceName);
		appendText(commandResult, textPane);
	}

	private void deleteService(String serviceName, JTextPane textPane) throws Exception {
		CommandResult commandResult = MyUtil.deleteService(serviceName);
		appendText(commandResult, textPane);
	}

	private void createService(String serviceName, String binPath, JTextPane textPane) throws Exception {
		CommandResult commandResult = MyUtil.createService(serviceName, binPath);
		appendText(commandResult, textPane);
	}

	private void replicaSetInit(String host, int port, String replicaSetId, String memberIp, int memberPort, boolean isConfig, JTextPane textPane, String mongodbHome) {
		try {
			appendTextLn(String.format("%s/bin/mongo --host %s --port %d", mongodbHome, host, port), textPane);
			appendTextLn(textPane);

			appendTextLn("use admin", textPane);
			appendTextLn(textPane);

			Document memberDoc = new Document();
			memberDoc.put("_id", 0);
			memberDoc.put("host", memberIp + ":" + memberPort);

			Document configDoc = new Document();
			configDoc.put("_id", replicaSetId);
			configDoc.put("members", Arrays.asList(memberDoc));
			if (isConfig) {
				configDoc.put("configsvr", true);
			}

			Document commandDoc = new Document();
			commandDoc.append("replSetInitiate", configDoc);

			appendTextRunCommandLn(commandDoc, textPane);

			Document resultDoc = MongoUtil.adminCommand(host, port, commandDoc);

			appendTextLn(resultDoc, textPane);

			appendTextLn(textPane);
			appendTextLn("exit", textPane);
		} catch (Exception e) {
			appendTextLn(e, textPane);
			throw e;
		}
	}

	private void mongosInit(String host, int port, String shardIp, int beginShardPort, int beginShardNumber, int endShardNumber, JTextPane textPane, String mongodbHome) {
		try {
			appendTextLn(String.format("%s/bin/mongo --host %s --port %d", mongodbHome, host, port), textPane);
			appendTextLn(textPane);

			String databaseName = "zd_pd_data_middle_platform";

			List<String> tableNameList = new ArrayList<>();
			tableNameList.add("t_busbar_ground_info");
			tableNameList.add("t_measure_breaker_info");
			tableNameList.add("t_measure_busbar_info");
			tableNameList.add("t_measure_line_info");
			tableNameList.add("t_measure_load_info");
			tableNameList.add("t_measure_sub_info");
			tableNameList.add("t_measure_tran_imbalance_info");
			tableNameList.add("t_measure_tran_info");
			tableNameList.add("t_measure_winding_info");
			tableNameList.add("t_peak_sudden_change");
			tableNameList.add("t_single_power_important_user_info");
			tableNameList.add("t_single_power_sub_info");
			tableNameList.add("t_sub_aisle_info");
			tableNameList.add("t_tran_temperature_info");

			MongoUtil.adminCallback(host, port, new MongoDatabaseCallback<Object>() {
				@Override
				public Object doWithMongoDatabase(MongoDatabase mongoDatabase) {
					appendTextLn("use admin", textPane);
					appendTextLn(textPane);

					// addShard
					for (int i = beginShardNumber; i <= endShardNumber; i++) {
						int shardPort = beginShardPort + i;

						Document commandDoc = new Document();
						commandDoc.put("addShard", String.format("shardReplicaSet%d/%s:%d", i, shardIp, shardPort));

						appendTextRunCommandLn(commandDoc, textPane);

						Document resultDoc = mongoDatabase.runCommand(commandDoc);

						appendTextLn(resultDoc, textPane);
						appendTextLn(textPane);
					}

					// enableSharding
					{
						Document commandDoc = new Document();
						commandDoc.put("enableSharding", databaseName);

						appendTextRunCommandLn(commandDoc, textPane);

						Document resultDoc = mongoDatabase.runCommand(commandDoc);

						appendTextLn(resultDoc, textPane);
						appendTextLn(textPane);
					}

					// shardCollection
					{
						for (String tableName : tableNameList) {
							Document commandDoc = new Document();
							commandDoc.put("shardCollection", databaseName + "." + tableName);
							commandDoc.put("key", new Document("occur_time", 1));

							appendTextRunCommandLn(commandDoc, textPane);

							Document resultDoc = mongoDatabase.runCommand(commandDoc);

							appendTextLn(resultDoc, textPane);
							appendTextLn(textPane);
						}
					}

					return null;
				}
			});

			MongoUtil.runCallback(host, port, databaseName, new MongoDatabaseCallback<Object>() {
				@Override
				public Object doWithMongoDatabase(MongoDatabase mongoDatabase) {
					appendTextLn("use " + databaseName, textPane);
					appendTextLn(textPane);

					// dropIndex
					for (String tableName : tableNameList) {
						MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(tableName);
						mongoCollection.dropIndex("occur_time_1");

						appendTextLn("db." + tableName + ".dropIndex(\"occur_time_1\")", textPane);
					}

					return null;
				}
			});

			appendTextLn(textPane);
			appendTextLn("exit", textPane);
		} catch (Exception e) {
			appendTextLn(e, textPane);
			throw e;
		}
	}

}
