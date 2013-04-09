package il.ac.huji.todolist;

import java.io.Serializable;
import java.util.Date;

class TodoItem implements Serializable, ITodoItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7910164253737970421L;
	String todoTitle;
	Date dueDate;
	
	public TodoItem(){
		todoTitle = "";
		dueDate = null;
	}
	
	public TodoItem(String title, Date date){
		todoTitle = title;
		dueDate = date;
	}
	
	public Date getDueDate() {
		return dueDate;
	}

	public String getTitle() {
		return todoTitle;
	}
	
}