package com.mel.util;


public class MathUtil
{
	
	public static final float PI = (float) Math.PI;

	public static float PI_TWICE = PI * 2.0f;
	public static float PI_HALF = PI * 0.5f;
	public static float PI_Q = PI * 0.25f;

	public static final float DEG_TO_RAD = PI / 180.0f;
	public static final float RAD_TO_DEG = 180.0f / PI;
	
	public static float getAngulo(double x0, double y0, double xD, double yD){
		double ang=0;
		double dy=yD-y0;
		double dx=xD-x0;
		if (dx>0) ang=Math.atan(dy/dx);
		else if (dx<0) ang=Math.atan(dy/dx)+Math.PI;
		else if (y0<yD) ang=Math.PI/2;
		else ang=-Math.PI/2;
		ang=corregirAngulo(ang);
		return (float)ang;
	}
	
	public static double corregirAngulo(double ang){
		while (ang<0)
			ang += Math.PI*2;
		while (ang>=Math.PI*2)
			ang -= Math.PI*2;
		return ang;
	}
	
	public static double getDistancia(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
	}
	
	public static final float radToDeg(final float pRad) {
		return RAD_TO_DEG * pRad;
	}

	public static final float degToRad(final float pDegree) {
		return DEG_TO_RAD * pDegree;
	}
	
    /**
     * Obte les coordenades que hi ha desde punt a una certa distancia i en una direccio indicada
     * @param punt Les coordeandes del punt origen
     * @param distancia La distancia del punt origen al punt objectiu
     * @param direccio L'angle en radiants de la posicio del punt objectiu vers al punt origen sobre el pla horitzonal
     * @return Retorna les coordenades del punt objectiu
     */
    public static Point getPuntDesti(Point origen, double direccio, float distancia) {
    	float x = (float)(origen.getX() + (distancia * Math.cos(direccio)));
    	float y = (float)(origen.getY() + (distancia * Math.sin(direccio)));
    	return new Point(x, y);
    }
    
    public static Point getPuntDesti(Point origen, Point pDireccio, float distancia) {
    	float direccio = MathUtil.getAngulo(origen.getX(), origen.getY(), pDireccio.getX(), pDireccio.getY());
		return getPuntDesti(origen, direccio, distancia);
    }
    
    
    /**
     * Obte un punt mes llunya del desti, en la mateixa trajectoria
     * @param origen El punt d'origen
     * @param desti El punt de desti
     * @param factor El factor d'allunyament respecte el punt desti (1=es queda al punt desti, 2=fa un recorregut del doble de distancia...)
     * @return El punt desti final 
     */
    public static Point getFurtherPoint(Point origen, Point desti, float factor) {
    	return getFurtherPoint(origen, desti, factor, -1);
    }
    
    public static Point getFurtherPoint(Point origen, Point desti, float factor, float max) {
		float distancia = (float)origen.distance(desti) * factor;
		if(max > 0){
			distancia = Math.min(max, distancia);
		}
		float direccio = MathUtil.getAngulo(origen.getX(), origen.getY(), desti.getX(), desti.getY());
		return MathUtil.getPuntDesti(origen, direccio, distancia);
    }
    
    public static Point getMiddlePoint(Point origen, Point desti) {
		return getFurtherPoint(origen, desti, 0.5f);
    }
    
    public static Point getRandomPointFromArea(Point origen, float distancia) {
    	float direccio = degToRad(org.andengine.util.math.MathUtils.random(0, 360));
    	distancia = (float)Math.random()*distancia;
    	return getPuntDesti(origen, direccio, distancia);
    }
}
