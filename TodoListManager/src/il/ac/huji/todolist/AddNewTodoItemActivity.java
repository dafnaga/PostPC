package il.ac.huji.todolist;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;

public class AddNewTodoItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_todo_item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_add_new_todo_item, menu);
        return true;
    }
    
	public void okButtonClicked(View view){
		Intent intent = getIntent();
		
		String title = ((EditText)findViewById(R.id.edtNewItem)).getText().toString();
				
		DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);	
		
		Date dueDate = null;
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
		dueDate = cal.getTime();		
		
		intent.putExtra(TodoListManagerActivity.NEW_ITEM_DUE_DATE, dueDate);
		intent.putExtra(TodoListManagerActivity.NEW_ITEM_TITLE, title);
		setResult(1, intent);
		finish();
	}
	
	public void cancelButtonClicked(View view){
		setResult(0);
		finish();		
	}
	
}

