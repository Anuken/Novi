package io.anuke.novi.sprites;

import java.util.*;

import io.anuke.novi.Novi;

//class for storing and sorting layers
public class LayerList{
	private Layer[] layerpool;
	public Layer[] layers;
	static int maxsize = 5600;
	public int count, lastcount, poolcount;
	boolean outofbounds;

	public LayerList(){
		layers = new Layer[maxsize];
		layerpool = new Layer[maxsize];
		for(int i = 0;i < maxsize;i ++){
			layerpool[i] = new Layer();
		}
	}

	public Layer addLayer(){
		if(poolcount >= maxsize - 1){
			Novi.log("--CRITICAL:-- Layer pool index out of bounds!");
			return null;
		}
		Layer layer = layerpool[poolcount ++];
		layer.clear();
		add(layer);
		return layer;
	}

	private void add(Layer l){
		if(l == null || count >= maxsize - 1){
			if( !outofbounds) Novi.log("--WARNING:-- Layer index out of bounds!");
			outofbounds = true;
			return;
		}
		layers[count] = l;
		count ++;
	}

	public synchronized void sort(){
		Arrays.sort(layers, new Comparator<Layer>(){
			@Override
			public int compare(Layer o1, Layer o2){
				if(o1 == null && o2 == null){
					return 0;
				}
				if(o1 == null){
					return 1;
				}
				if(o2 == null){
					return -1;
				}
				return o1.compareTo(o2);
			}
		});
	}

	public synchronized void clear(){
		for(int i = 0;i < maxsize;i ++){
			layers[i] = null;
		}
		lastcount = count;
		count = 0;
		outofbounds = false;
		poolcount = 0;
	}
}
