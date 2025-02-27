package de.hft_stuttgart.swp2.render.threads;

import de.hft_stuttgart.swp2.opencl.ShadowPrecision;
import de.hft_stuttgart.swp2.render.Main;
import de.hft_stuttgart.swp2.render.options.PanelCityInfo;


public class StartShadowCalculationRunnable  implements Runnable{
	private ShadowPrecision shadowPrecision;
	private static boolean isShadowCalculated = false;
	private int splitAzimuth;
	private int splitHeight;
	public StartShadowCalculationRunnable(ShadowPrecision shadowPrecision, int splitAzimuth, int splitHeight){
		this.shadowPrecision = shadowPrecision;
		this.splitAzimuth = splitAzimuth;
		this.splitHeight = splitHeight;
	}

	@Override
	public void run() {
		setShadowCalculated(true);
		String oldText = Main.getCityMap3D().getTitle();
		Main.getOptionGUI().getBtnStartParseOfPanelSettings().setEnabled(false);
		Main.getOptionGUI().getBtnRecalculateShadow().setEnabled(false);
		Main.getCityMap3D().setTitle(oldText + " | calulate shadow");
		Main.getCityMap3D().stopAnimator();
		Main.calculateShadow(shadowPrecision, splitAzimuth, splitHeight);
		Main.getCityMap3D().startAnimator();
		Main.getOptionGUI().getBtnRecalculateShadow().setEnabled(true);
		Main.getOptionGUI().getBtnStartParseOfPanelSettings().setEnabled(true);
		oldText = "vCity - 3D Stadtansicht:"
				+ " " + Main.getOptionGUI().getFileName();
		Main.getCityMap3D().setTitle(oldText);
		Main.getOptionGUI().setTitleOfCityMap(Main.getOptionGUI().getTime().getTime());
		if(Main.isCityAndBuildingsCalculated()){
			PanelCityInfo.updateCityInfo();	
		}
		Main.getOptionGUI().setSelectShadowView(true);
		Main.getOptionGUI().setTitleOfBtnRecalculateShadow("Neu rechnen");
	}

	public static boolean isShadowCalculated() {
		return isShadowCalculated;
	}

	public static void setShadowCalculated(boolean isShadowCalculated) {
		StartShadowCalculationRunnable.isShadowCalculated = isShadowCalculated;
	}

}