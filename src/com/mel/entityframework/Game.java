package com.mel.entityframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Game
{
	public Map<Class, List<IEntity>> entities = new HashMap<Class, List<IEntity>>();
	public List<Process> processes = new ArrayList<Process>();
	
	public Game(){
		
	}
	
	public void addEntities(List entities){
		for(Object entity:entities){
			addEntity((IEntity)entity);
		}
	}
	
	public void addEntity(IEntity entity){
		if(this.entities.containsKey(entity.getClass())==false){
			this.entities.put(entity.getClass(), new ArrayList<IEntity>());
		}
		
		this.entities.get(entity.getClass()).add(entity);
	}
	
	public List getEntities(Object entityType){
		return new ArrayList<IEntity>(this.entities.get(entityType));
	}
	
	public IEntity getEntity(Object entityType){
		return (IEntity)this.entities.get(entityType).get(0);
	}
	
	public void removeEntity(IEntity entity){
		List entitiesList = this.entities.get(entity.getClass());
		entitiesList.remove(entity);
		entity.recycle();
	}
	
	public void addProcess(Process system, int priority){
		system.priority = priority;
		system.onAddToGame( this );
		this.processes.add(system);
		Collections.sort(this.processes);
	}
	
	public void removeProcess(Process system){
		this.processes.remove(system);
		//Collections.sort(this.systems); //no deberia ser necesario al quitar un elemento
	}
	
	public void update(){
		for(Process process:this.processes){
			process.update();
		}
	}
}
