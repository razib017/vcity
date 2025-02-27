package de.hft_stuttgart.swp2.render.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.options.navigation.PanelNavigation;


public class OptionGUI extends JFrame implements Refreshable{
	
	private int frameHeight; //Stores the height before refresh
	private final int PREF_HEIGHT = this.getToolkit().getScreenSize().height-70;
	private final int PREF_WIDTH= 300;
	private JPanel panelCityInfo = new PanelCityInfo();
	private JPanel panelNavigation = new PanelNavigation();
	private JPanel panelInformation = new PanelInformation();
	private JButton btn1 = new JButton("Einstellungen");
	private JButton btnCombine = new JButton("+");
	private JButton btn2 = new JButton("Stadtinfo");
	private JButton btn3 = new JButton("Steuerung");
	JPanel topPanel = new JPanel();
	private static JPanel content_panel;
	
	private JScrollPane scrollPane; 
	private GridBagConstraints constraints = new GridBagConstraints();

	private static final long serialVersionUID = -2135256125525996134L;

	private final int INSET_TOP_PANEL= 5;
	private final int INSET_LEFT_PANEL= 10;
	private final int INSET_RIGHT_PANEL= 0;
	private final int INSET_BOTTOM_PANEL= 0;
	
	private final int INSET_TOP_BUTTON= 5;
	private final int INSET_LEFT_BUTTON= 0;
	private final int INSET_RIGHT_BUTTON= 0;
	private final int INSET_BOTTOM_BUTTON= 0;
	
	private final Insets INSET_PANEL = new Insets(INSET_TOP_PANEL, 
			INSET_LEFT_PANEL, INSET_BOTTOM_PANEL, INSET_RIGHT_PANEL);
	private final Insets INSET_BUTTON = new Insets(INSET_TOP_BUTTON, 
			INSET_LEFT_BUTTON, INSET_BOTTOM_BUTTON, INSET_RIGHT_BUTTON);
	
	private PanelSettings panelSettings = new PanelSettings();
	
	public Boolean isCalculateVolume(){
		return panelSettings.cbVolume.isSelected();
	}
	
	public Boolean isCalculateShadow(){
		return panelSettings.isCbShadowIsSelected();
	}
	
	public boolean isVolumeViewSelected(){
		return panelSettings.isVolumeViewSelected();
	}
	
	public void setVolumeViewEnabled(boolean enabled){
		panelSettings.setVolumeViewEnabled(enabled);
	}
	
	public void setShadowViewEnabled(boolean enabled){
		panelSettings.setShadowViewEnabled(enabled);
	}
	
	public void setCbVolumeAmount(boolean isVisible){
		panelSettings.setCbVolumeAmount(isVisible);
	}
	
	public void setSelectVolumeView(boolean select){
		panelSettings.setSelectVolumeView(select);
	}
	
	public void setSelectShadowView(boolean select){
		panelSettings.setSelectShadowView(select);
	}
	
	public void setTitleOfBtnRecalculateShadow(String title){
		panelSettings.setTitleOfBtnRecalculateShadow(title);
	}
	
	public boolean isShadowViewSelected(){
		return panelSettings.isShadowViewSelected();
	}
	
	public JButton getBtnStartParseOfPanelSettings() {
		return panelSettings.getBtnStartParse();
	}
	
	public JButton getBtnRecalculateShadow() {
		return panelSettings.getBtnRecalculateShadow();
	}
	
	public void setBtnExportEnabled(boolean enabled){
		panelSettings.setBtnExportEnabled(enabled);
	}
	
	public GregorianCalendar getTime(){
		return panelSettings.getTime();
	}
	
	public void setTime(Date userDate, int hours, int minutes){
		panelSettings.setTime(userDate, hours, minutes);
	}
	
	public void setHours(int hours){
		panelSettings.setHours(hours);
	}
	
	public void setMinutes(int minutes){
		panelSettings.setMinutes(minutes);
	}
	
	public void setTitleOfCityMap(Date oldDate) {
		panelSettings.setTitleOfCityMap(oldDate);
	}

	JPanel panelExport = new PanelExport();

	public String getFileName (){
		return panelSettings.getGmlFileName();
	}
	public OptionGUI() {
		this.setLocation(this.getToolkit().getScreenSize().width- 315, 0);
		this.setTitle("vCity - Einstellungen");
		scrollPane = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		scrollPane.setViewportView( content_panel );
		topPanel.setLayout(new BorderLayout());
		setPanelContent();
		topPanel.add(content_panel, BorderLayout.CENTER);
		this.setContentPane(topPanel);
		this.setMaximumSize(new Dimension(500,2000));
//		this.add(panelExport, BorderLayout.EAST);
		panelExport.setVisible(false);
		panelInformation.setVisible(false);
		content_panel.setVisible(true);
		this.setVisible(true);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		this.setLocationRelativeTo(null);
		frameHeight = this.getHeight();
	}


	private void setPanelContent() {
		if(content_panel == null){
			content_panel = new JPanel();
			content_panel.setPreferredSize(new Dimension(PREF_WIDTH,PREF_HEIGHT));
			content_panel.setLayout(new GridBagLayout());
			
			addBtn1();
			addPanelSettings();
			addBtn2();
			addPanelCityInfo();
			addBtn3();
			addPanelNavigation();
			

			
			panelSettings.setVisible(true);
			panelCityInfo.setVisible(true);
			panelNavigation.setVisible(true);
			addButtonActionListeners();
		}else{
			btnCombine.setText("+");
		}
	}
	
	private void addBtn1(){
		constraints.insets = new Insets(0, INSET_LEFT_BUTTON, 
				INSET_BOTTOM_BUTTON, INSET_RIGHT_BUTTON);
		constraints.weightx = 0.5;// components
		constraints.weighty = 0;   //request any extra vertical space
		constraints.gridx = 0; // column 0
		constraints.gridy = 0; // row 0
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		JPanel panelSettingButtons = createPanelSettingButtons();

		content_panel.add(panelSettingButtons, constraints);
	}
	
	private JPanel createPanelSettingButtons() {
		JPanel panelSettingButtons = new JPanel();
		panelSettingButtons.setLayout(new GridBagLayout());
		GridBagConstraints constraintsPanelSettingsButtons = new GridBagConstraints();
		constraintsPanelSettingsButtons.weightx = 0.5;// components
		constraintsPanelSettingsButtons.weighty = 0;   //request any extra vertical space
		constraintsPanelSettingsButtons.gridx = 0; // column 0
		constraintsPanelSettingsButtons.fill = GridBagConstraints.BOTH;
		constraintsPanelSettingsButtons.gridy = 0; // row 0
		constraintsPanelSettingsButtons.gridwidth = 3; // row 0
		panelSettingButtons.add(btn1,constraintsPanelSettingsButtons);
		constraintsPanelSettingsButtons.weightx = 0.2;// components
		constraintsPanelSettingsButtons.gridwidth = 1; // row 0
		constraintsPanelSettingsButtons.gridx = 4; // column 0
		panelSettingButtons.add(btnCombine,constraintsPanelSettingsButtons);
		btnCombine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				combineToCityMap();
			}
		});
		return panelSettingButtons;
	}
	
	public void updateBtnCombine(){
		if(btnCombine.getText().equals("-")){
			btnCombine.setText("+");
		}
	}
	
	private void combineToCityMap(){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(btnCombine.getText().equals("+")){
					Thread t = new Thread(new Runnable() {
					      @Override
				            public void run() {
								btnCombine.setText("-");
								Main.getCityMap3D().add(topPanel, BorderLayout.EAST);
								Main.getCityMap3D().revalidate();
								Main.getOptionGUI().setVisible(false);
								Main.getOptionGUI().dispose();
				            }
				        });
					t.start();
				}else{
					Thread t = new Thread(new Runnable() {
			            @Override
			            public void run() {
							btnCombine.setText("+");
							Main.getCityMap3D().remove(topPanel);
							Main.getCityMap3D().repaint();
							Main.getCityMap3D().revalidate();
							Main.getOptionGUI().remove(topPanel);
							Main.getOptionGUI().setContentPane(topPanel);
							int extraSize = 0;
							if(isPanelExportVisible()){
								extraSize = panelExport.getWidth();
							}else if(isPanelInformationVisible()){
								extraSize = panelInformation.getWidth();
							}
							Main.getOptionGUI().setLocation(
									Main.getOptionGUI().getToolkit().getScreenSize().width- (315+extraSize), 0);
							Main.getOptionGUI().pack();
							Main.getOptionGUI().setVisible(true);
			            }
			        });
					t.start();
				}
			}
		});
	}

	private void addBtn2() {
		constraints.insets = INSET_BUTTON;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridheight = 1;
		constraints.weighty = 0;   //request any extra vertical space
		constraints.fill = GridBagConstraints.HORIZONTAL;
		content_panel.add(btn2, constraints);
	}
	
	private void addBtn3() {
		constraints.insets = INSET_BUTTON;
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weighty = 0;   //request any extra vertical space
		content_panel.add(btn3, constraints);
	}	

	private void addPanelSettings() {
		panelSettings.setPreferredSize(new Dimension(250,450));
		panelSettings.setMinimumSize(new Dimension(250,350));
		JScrollPane jspSettings = new JScrollPane(panelSettings);
		//jspSettings.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		constraints.insets = INSET_PANEL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridheight = 1;
		constraints.weighty = 3.0; //request any extra vertical space  
		constraints.fill = GridBagConstraints.BOTH;
		jspSettings.setMinimumSize(new Dimension(250,450));
		content_panel.add(jspSettings, constraints);
	}
	
	private void addPanelCityInfo() {
		constraints.insets = INSET_PANEL;
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridheight = 1;
		constraints.weighty = 2.0; //request any extra vertical space 
		constraints.fill = GridBagConstraints.BOTH;
		panelCityInfo.setMinimumSize(new Dimension(200,200));
		content_panel.add(panelCityInfo, constraints);
	}
	
	public JPanel getContent_panel() {
		return content_panel;
	}

	private void addPanelNavigation() {
		constraints.insets = INSET_PANEL;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 1.0; //request any extra vertical space  
		constraints.anchor = GridBagConstraints.PAGE_END; //bottom of space
		constraints.gridx = 0;       //aligned with button 2
		constraints.gridwidth = 1;   //2 columns wide
		constraints.gridy = 5;       //third row
		panelNavigation.setMinimumSize(new Dimension(200,200));
		content_panel.add(panelNavigation, constraints);
	}
	
	private void addButtonActionListeners() {
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(panelSettings.isVisible()){
					panelSettings.setVisible(false);
				}else{
					panelSettings.setVisible(true);
				}
				refresh();
			}
		});
		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(panelCityInfo.isVisible()){
					panelCityInfo.setVisible(false);
				}else{
					panelCityInfo.setVisible(true);
				}
				refresh();
			}
		});
		btn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(panelNavigation.isVisible()){
					panelNavigation.setVisible(false);
				}else{
					panelNavigation.setVisible(true);
				}
				refresh();
			}
		});
	}
	
	public String getPath() {
		return panelSettings.getPath();
	}
	public void removePanelInformation(){
		this.remove(panelInformation);
		this.pack();
	}
	public boolean isPanelInformationVisible(){
		return panelInformation.isVisible();
	}
	
	public void openOrCloseInformationPanel(){
		panelSettings.openOrCloseInformationPanel();
	}
	public void addPanelInformation(){
		if(!panelInformation.isVisible()){
			int currentHeight = this.getHeight();
			int currentWidth = this.getWidth();
			panelInformation.setVisible(true);
			topPanel.add(panelInformation, BorderLayout.WEST);
			topPanel.revalidate();
			this.pack();
			this.setSize(new Dimension(currentWidth, currentHeight));
			Main.getOptionGUI().setLocation(
					Main.getOptionGUI().getToolkit().getScreenSize().width- 
					(315+panelInformation.getWidth()), 0);
		}
	}
	
	public void setPanelInformationVisible(boolean visible){
		if(visible == true){
			panelInformation.setVisible(true);
		}else {
			panelInformation.setVisible(false);
		}
	}
	
	public void setPanelExportVisible(boolean visible){
		if(visible == true){
			panelExport.setVisible(true);
		}else {
			panelExport.setVisible(false);
		}
	}
	
	public void removePanelExport(){
		this.remove(panelExport);
		this.pack();
	}
	public boolean isPanelExportVisible(){
		return panelExport.isVisible();
	}
	public void addPanelExport(){
		if(!panelExport.isVisible()){
			int currentHeight = this.getHeight();
			int currentWidth = this.getWidth();
			panelExport.setVisible(true);
			topPanel.add(panelExport, BorderLayout.WEST);
			topPanel.revalidate();
			this.pack();
			this.setSize(new Dimension(currentWidth, currentHeight));
			Main.getOptionGUI().setLocation(
					Main.getOptionGUI().getToolkit().getScreenSize().width- 
					(315+panelExport.getWidth()), 0);
		}
	}
	
	@Override
	public void refresh(){
		boolean allClosed = true;
		int size = 0;
		content_panel.removeAll();
		addBtn1();
		size = size + btn1.getHeight();
		if(panelSettings.isVisible()){
			addPanelSettings();
			size = size + panelSettings.getHeight();
			allClosed = false;
		}
		addBtn2();
		size = size + btn2.getHeight();
		if(panelCityInfo.isVisible()){
			addPanelCityInfo();
			size = size + panelCityInfo.getHeight();
			allClosed = false;
		}
		
		addBtn3();
		size = size + btn3.getHeight();
		if(panelNavigation.isVisible()){
			addPanelNavigation();
			size = size + panelNavigation.getHeight();
			allClosed = false;
		}
		if(allClosed){
			content_panel.setPreferredSize(new Dimension(PREF_WIDTH, size));
			// + 40, because of the insets from the panels, that are only hidden
			this.setSize(new Dimension(this.getWidth(), size + 40));
			this.setResizable(false);
		}else{
			content_panel.setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
			this.setSize(new Dimension(this.getWidth(), frameHeight));
			this.setResizable(true);
		}
		content_panel.revalidate();
		this.pack();
	
	}
	
	public JPanel getContent_Panel(){
		return content_panel;
	}


}
