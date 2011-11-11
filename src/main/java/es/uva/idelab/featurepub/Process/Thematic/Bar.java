package es.uva.idelab.featurepub.Process.Thematic;

import es.uva.idelab.featurepub.Process.Data.DataDAO;

public class Bar extends Collada {

	public Bar(DataDAO dataDAO, String thematicAttribute, double thematicScaleX, double thematicScaleY, double thematicScaleZ, String colladaLink) {
		super(dataDAO, thematicAttribute, thematicScaleX, thematicScaleY, thematicScaleZ, colladaLink);
	}

	public void putScales(){
		
		collada.put("scaleX", thematicScaleX);
		collada.put("scaleY", thematicScaleY);
		collada.put("scaleZ", thematicScaleZ * thematicAttributeValue.doubleValue());

	}
	
}
