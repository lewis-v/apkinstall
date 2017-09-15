package com.ude.install;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.*;

public class UI {
	static Process process;
	Thread thread_get,thread_err;
	BufferedReader reader;
	InputStream input;
	BufferedReader  errorReader;
	
	JFrame jframe;
	JButton bt_con,bt_install;
	JTextArea text;
	JFileChooser jFileChooser;
	JTextField textFile;
	JPanel devices_box;
	JPanel p2;
	JPanel panel_devices;
	
	boolean iscon = false;
	boolean isget = false;
	
	static int install_count = 0,count = 0,install_fail = 0;
	
	List<String> devices_list = new CopyOnWriteArrayList<>();
	List<String> devices_name = new CopyOnWriteArrayList<>();
	List<JCheckBox> list_box = new CopyOnWriteArrayList<>();
	List<String> selected_list = new CopyOnWriteArrayList<>();
	
	public UI(){
		Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();//获取屏幕的大小,在具有多个显示屏的系统上，使用主显示屏
		jframe = new JFrame("apk安装器");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setLayout(new BorderLayout());
		jframe.setBounds(screenSize.width/4,screenSize.height/4, screenSize.width/4,screenSize.height/4);
		jframe.setResizable(false);
		
		text = new JTextArea(10,10);
		text.setLineWrap(true); //自动换行
		text.setEditable(false);//不可编辑
		JScrollPane scroll = new JScrollPane(text); 
		scroll.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		scroll.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(scroll,BorderLayout.CENTER);
		
		bt_con = new JButton("连接设备");
		bt_con.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				new Thread(){
					public void run(){
						devices_list.clear();
						devices_name.clear();
						list_box.clear();
						devices_box.removeAll();
						iscon = false;
						isget = false;
						 try {
								UI.this.writeADB("adb devices");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						 
//						 while(!iscon){
//							 try {
//								sleep(100);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//								break;
//							}
//						 }
//						 
//						 for(String str : devices_list){
//							 try {
//								 isget = false;
//								UI.this.writeADB("adb -s "+str+" shell cat /system/build.prop");
//								System.out.println("111111111111111111111111111111111111111111111111");
//								while(!isget){
//									sleep(100);
//							 }
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							 
//						 }
						 
						 
						 new Thread(){
							 public void run(){
								 while(!iscon){
									 try {
										sleep(100);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										break;
									}
								 }
								 panel_devices = new JPanel(new GridLayout(devices_name.size(),1));
								 for(String name:devices_name){
									 JCheckBox b = new JCheckBox(name);
									 list_box.add(b);
									 panel_devices.add(b);
								 System.out.println(name);
								 }
								 devices_box.add(new JLabel("连接的设备:"),BorderLayout.NORTH);
								 devices_box.add(panel_devices,BorderLayout.CENTER);
								 p2.add(devices_box, BorderLayout.CENTER);
								 jframe.setSize(screenSize.width/4+1,screenSize.height/4); 
								 jframe.setSize(screenSize.width/4,screenSize.height/4); 
								 jframe.repaint(); 
								 jframe.pack();
							 }
						 }.start();
					}
				}.start();
				
			}
			
		});
		
		bt_install = new JButton("安装");
		bt_install.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(list_box.size() == 0){
					JOptionPane.showMessageDialog(null, "请连接设备", "未连接设备", JOptionPane.ERROR_MESSAGE);
				}else if(textFile.getText().trim().length() == 0){
					JOptionPane.showMessageDialog(null, "请选择apk安装包", "为选择安装包", JOptionPane.ERROR_MESSAGE);
				}else if(!textFile.getText().endsWith("apk")){
					JOptionPane.showMessageDialog(null, "请选择apk安装包", "文件类型不正确", JOptionPane.ERROR_MESSAGE);
				}else if(!(new File(textFile.getText()).exists())){
					JOptionPane.showMessageDialog(null, "请选择正确的apk安装包", "文件出错", JOptionPane.ERROR_MESSAGE);
				}else{
					selected_list.clear();
					new Thread(){
						public void run(){
							install_count = 0;
							for(int i = 0;i<list_box.size();i++){
								if(list_box.get(i).isSelected()){
									install_count++;
								}}
							count = 0;
							install_fail = 0;
							for(int i = 0;i<list_box.size();i++){
								if(list_box.get(i).isSelected()){
									selected_list.add(devices_list.get(i));
									try {
										UI.this.writeADB("adb -s "+devices_list.get(i)+" install "+textFile.getText());
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (InterruptedException eq) {
										// TODO Auto-generated catch block
										eq.printStackTrace();
									}
								}
					}
						}
					}.start();
					
					
				}
				
			}
			
		});
		
		JPanel panel_bt = new JPanel(new FlowLayout());
		panel_bt.add(bt_con);
		panel_bt.add(bt_install);
		
		devices_box = new JPanel(new BorderLayout());
		devices_box.add(new JLabel("连接的设备:"),BorderLayout.NORTH);
		devices_box.add(new JLabel("请连接设备"));
		
		  
        textFile = new JTextField(30);
        JButton bt_file = new JButton("选择安装包");
        bt_file.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				jFileChooser=new JFileChooser();  
				jFileChooser.setCurrentDirectory(new File("."));  
				jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
				jFileChooser.showDialog(new JLabel(), "选择安装包"); 
		        File file=jFileChooser.getSelectedFile();  
		        if(file != null){
		        	if(!file.getAbsolutePath().endsWith("apk")){
		        		JOptionPane.showMessageDialog(null, "请选择apk安装包", "选错啦", JOptionPane.ERROR_MESSAGE);
		        	}else{
		        		textFile.setText(file.getAbsolutePath());
		        	}
		        }
			}
        	
        });
        
        JPanel panel_fiel = new JPanel(new FlowLayout());
        panel_fiel.add(textFile);
        panel_fiel.add(bt_file);
		
		p2 = new JPanel(new BorderLayout());
		p2.add(devices_box,BorderLayout.CENTER);
		p2.add(panel_bt,BorderLayout.EAST);
		p2.add(panel_fiel,BorderLayout.NORTH);
		
		
		jframe.add(p1,BorderLayout.CENTER);
		jframe.add(p2,BorderLayout.SOUTH);
		jframe.pack();
        jframe.setVisible(true);
        
       
	   
	}
	/**
	 * 输入adb命令
	 * @param str
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void writeADB(String str) throws IOException, InterruptedException{
		 Runtime run = Runtime.getRuntime();           
		 process = run.exec(str);   
		 System.out.println(str);
		 text.append(str);
		 text.selectAll();  
		 text.setCaretPosition(text.getSelectedText()  
		       .length());  
		 text.requestFocus(); 
		 startInput();
		 process.waitFor();   
		 process.destroy(); 
	}
	
	/**
	 * 打开新的接收流
	 */
	public void startInput(){
		if(thread_get != null){
			thread_get.interrupt();
		}
		if(thread_err != null){
			thread_err.interrupt();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startGetStream();
		startGetErr();
	}
	/**
	 * 打开读取信息流
	 */
	public void startGetStream(){
		thread_get = new Thread(){
			public void run(){
				input = process.getInputStream();   
		        reader = new BufferedReader(new InputStreamReader(input));  
				while(true){
					try {
						sleep(1);
					} catch (InterruptedException e) {
						break;
					}
			        String szline;  
			        try {
						if ((szline = reader.readLine())!= null) {     
						    System.out.println("info:"+szline);     
						    text.append(szline+"\n");
							 text.selectAll();  
							 text.setCaretPosition(text.getSelectedText()  
							       .length());  
							 text.requestFocus();
							 if(szline.endsWith("device")){
								 //设备号
								 devices_list.add(szline.substring(0, szline.indexOf("device")).trim());
								 devices_name.add(szline.substring(0, szline.indexOf("device")).trim());
								 System.out.println(devices_list.toString());
							 }
							 if(szline.equals("")){
								 iscon = true;
							 }
//							 if(szline.startsWith("ro.product.model=")){
//								 //设备名
//								 devices_name.add(szline.substring(szline.indexOf("=")+1).trim());
//								 System.out.println(devices_name.toString());
//							 }
							 if(szline.trim().startsWith("pkg")){
								 text.append("正在安装.......\n");
							 }
							 if(szline.equals("Success")){
								 count++;
								 text.append("安装成功.......("+count+"/"+install_count+")\n");
								 if(count+install_fail == install_count){
									 text.append("安装完成("+install_count+"个任务,"+count+"个成功,"+install_fail+"个失败)\n");
								 }
							 }
							 if(szline.startsWith("Failure")){
								 install_fail++;
								 text.append("安装失败.......("+install_fail+"/"+install_count+")\n");
								 if(count+install_fail == install_count){
									 text.append("安装完成("+install_count+"个任务,"+count+"个成功,"+install_fail+"个失败)\n");
								 }
							 }if(szline.startsWith("ro.expect.recovery_id=")){
								 isget = true;
							 }
							 
						} 
					} catch (IOException e) {
						input = process.getInputStream();   
						reader = new BufferedReader(new InputStreamReader(input));
					}   
				}
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread_get.start();
	}
	
	/**
	 * 打开读取信息流
	 */
	public void startGetErr(){
		thread_err = new Thread(){
			public void run(){
				errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream())); 
				while(true){
					try {
						sleep(1);
					} catch (InterruptedException e) {
						break;
					}
			        String szline;  
			        try {
			        	if ((szline = errorReader.readLine())!= null) {     
						    System.out.println("err:"+szline);   
						    text.append(szline);
							 text.selectAll();  
							 text.setCaretPosition(text.getSelectedText()  
							       .length());  
							 text.requestFocus();
						} 
					} catch (IOException e) {
						errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream())); 
					}   
				}
				try {
					errorReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread_err.start();
	}

}
