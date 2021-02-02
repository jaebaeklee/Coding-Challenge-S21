import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import org.biojava.bio.seq.FeatureFilter;
import org.biojava.bio.seq.StrandedFeature;
import org.biojava.bio.symbol.RangeLocation;

import java.util.Scanner;

import ca.corefacility.gview.data.GenomeData;
import ca.corefacility.gview.data.Slot;
import ca.corefacility.gview.data.readers.GViewDataParseException;
import ca.corefacility.gview.data.readers.GViewFileData;
import ca.corefacility.gview.data.readers.GViewFileReader;
import ca.corefacility.gview.layout.sequence.LayoutFactory;
import ca.corefacility.gview.layout.sequence.circular.LayoutFactoryCircular;
import ca.corefacility.gview.map.GViewMap;
import ca.corefacility.gview.map.GViewMapFactory;
import ca.corefacility.gview.style.GlobalStyle;
import ca.corefacility.gview.style.MapStyle;
import ca.corefacility.gview.style.datastyle.DataStyle;
import ca.corefacility.gview.style.datastyle.FeatureHolderStyle;
import ca.corefacility.gview.style.datastyle.SlotStyle;
import ca.corefacility.gview.style.items.BackboneStyle;
import ca.corefacility.gview.style.items.RulerStyle;
import ca.corefacility.gview.style.items.TooltipStyle;
import ca.corefacility.gview.textextractor.LocationExtractor;
import ca.corefacility.gview.writers.ImageWriter;
import ca.corefacility.gview.writers.ImageWriterFactory;

public class main {
	private static MapStyle buildStyle() { // creates style for the builder at the end. have no idea what to prioritize
											// so used default values from library
		// no idea what to label either to be quite honest, trusting default values as
		// well for that

		/** Global Style **/

		MapStyle mapStyle = new MapStyle();

		GlobalStyle global = mapStyle.getGlobalStyle();

		global.setDefaultWidth(1200);
		global.setDefaultHeight(900);

		global.setBackgroundPaint(Color.WHITE);

		TooltipStyle tooltip = global.getTooltipStyle();
		tooltip.setFont(new Font("SansSerif", Font.PLAIN, 12));
		tooltip.setBackgroundPaint(new Color(134, 134, 255));
		tooltip.setOutlinePaint(new Color(0.0f, 0.0f, 0.0f, 0.5f));
		tooltip.setTextPaint(Color.BLACK);
		BackboneStyle backbone = global.getBackboneStyle();
		backbone.setPaint(Color.GRAY.darker());
		backbone.setThickness(2.0);
		backbone.setInitialBackboneLength(1100);
		RulerStyle ruler = global.getRulerStyle();
		ruler.setMajorTickLength(15.0);
		ruler.setMinorTickLength(5.0);
		ruler.setTickDensity(0.5f);
		ruler.setTickThickness(2.0);
		ruler.setMinorTickPaint(Color.GREEN.darker().darker());
		ruler.setMajorTickPaint(Color.GREEN.darker().darker());
		ruler.setFont(new Font("SansSerif", Font.BOLD, 12));
		ruler.setTextPaint(Color.BLACK);

		/** Slots **/

		DataStyle dataStyle = mapStyle.getDataStyle();

		SlotStyle firstUpperSlot = dataStyle.createSlotStyle(Slot.FIRST_UPPER);
		SlotStyle firstLowerSlot = dataStyle.createSlotStyle(Slot.FIRST_LOWER);

		SlotStyle secondUpperSlot = dataStyle.createSlotStyle(Slot.FIRST_UPPER + 1);
		SlotStyle secondLowerSlot = dataStyle.createSlotStyle(Slot.FIRST_LOWER - 1);

		firstUpperSlot.setPaint(Color.BLACK);
		firstLowerSlot.setPaint(Color.BLACK);
		secondUpperSlot.setPaint(Color.BLACK);
		secondLowerSlot.setPaint(Color.BLACK);

		firstUpperSlot.setThickness(15);
		firstLowerSlot.setThickness(15);
		secondUpperSlot.setThickness(15);
		secondLowerSlot.setThickness(15);

		/** FeatureHolderStyle **/

		FeatureHolderStyle positiveFeatures = firstUpperSlot
				.createFeatureHolderStyle(new FeatureFilter.StrandFilter(StrandedFeature.POSITIVE));
		positiveFeatures.setThickness(0.7);

		positiveFeatures.setTransparency(0.9f);
		positiveFeatures.setToolTipExtractor(new LocationExtractor());

		positiveFeatures.setPaint(Color.BLUE);
		FeatureHolderStyle subPositiveFeatures = positiveFeatures
				.createFeatureHolderStyle(new FeatureFilter.OverlapsLocation(new RangeLocation(50, 95)));
		subPositiveFeatures.setPaint(Color.BLUE.darker().darker().darker().darker());

		FeatureHolderStyle negativeFeatures = firstLowerSlot
				.createFeatureHolderStyle(new FeatureFilter.StrandFilter(StrandedFeature.NEGATIVE));
		negativeFeatures.setThickness(0.7);
		negativeFeatures.setToolTipExtractor(new LocationExtractor());
		negativeFeatures.setPaint(Color.RED);

		FeatureHolderStyle allFeatures = secondUpperSlot.createFeatureHolderStyle(FeatureFilter.all);
		allFeatures.setThickness(0.7);
		allFeatures.setToolTipExtractor(new LocationExtractor());
		allFeatures.setPaint(Color.GREEN);

		FeatureHolderStyle locationFeatures = secondLowerSlot
				.createFeatureHolderStyle(new FeatureFilter.OverlapsLocation(new RangeLocation(0, 50)));
		locationFeatures.setThickness(0.7);
		locationFeatures.setToolTipExtractor(new LocationExtractor());
		locationFeatures.setPaint(Color.BLACK);

		return mapStyle;
	}

	public static void main(String[] args) throws IOException, GViewDataParseException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("name of file: ");
		String fileName = scanner.nextLine();
		GViewFileData fileData = GViewFileReader.read(fileName);
		GenomeData data = fileData.getGenomeData();
		MapStyle style = buildStyle();
		LayoutFactory layoutFactory;

		layoutFactory = new LayoutFactoryCircular();

		GViewMap gViewMap = GViewMapFactory.createMap(data, style, layoutFactory);

		gViewMap.setViewSize(1200, 600); // again, default values turned out fine

		gViewMap.centerMap();

		ImageWriter writerPNG = ImageWriterFactory.createImageWriter("png");

		try { // try catch
			writerPNG.writeToImage(gViewMap, "map.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
