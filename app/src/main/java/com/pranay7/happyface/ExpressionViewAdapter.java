package com.pranay7.happyface;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.pranay7.happyface.database.Expression;
import com.pranay7.happyface.databinding.ExpressionLayoutBinding;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public class ExpressionViewAdapter extends RecyclerView.Adapter<ExpressionViewAdapter.ExpressionViewHolder> {


    @NonNull
    @Override
    public ExpressionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expression_layout,parent,false);
        return new ExpressionViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ExpressionViewHolder holder, int position) {
        String  dateTime= data.get(position).getDateTime();
        int year = LocalDateTime.parse(dateTime).getYear();
        int day = LocalDateTime.parse(dateTime).getDayOfMonth();
        Month month1 = LocalDateTime.parse(dateTime).getMonth();
        String month = month1.toString();

        holder.binding.dateTextView.setText("Date: "+day+" "+month+", "+year);
        int hour = LocalDateTime.parse(dateTime).getHour();
        String amPm = "am";
        if(hour>12){
            hour = hour -12;
            amPm = "pm";
        }
        int min = LocalDateTime.parse(dateTime).getMinute();
        String minute=""+min;
        if(min<10){
            minute = "0"+min;
        }

        holder.binding.timeTextView.setText("Time: "+hour+":"+minute+amPm);

        holder.binding.expressionsTextView.setText("Expression: "+data.get(position).getExpression());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private List<Expression> data;

    public ExpressionViewAdapter(List<Expression> data) {
        this.data = data;
    }

    public class ExpressionViewHolder extends RecyclerView.ViewHolder{
        ExpressionLayoutBinding binding;

        public ExpressionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ExpressionLayoutBinding.bind(itemView);
        }
    }
}
