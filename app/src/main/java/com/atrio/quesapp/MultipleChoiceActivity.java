package com.atrio.quesapp;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.atrio.quesapp.model.QuessAnsModel;
import com.atrio.quesapp.model.QuestionModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MultipleChoiceActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    String tittle,lang,qno_list,correct_ans,selectedAns;
    int qno = 001 ,checkedRadioButtonID;
    TextView tv_tittle,tv_score,tv_quess,tv_correct;
    Button bt_next;
    FirebaseUser user;
    DatabaseReference m_db;
    private FirebaseAuth mAuth;
    RadioGroup rd_grp;
    RadioButton rb_opA,rb_opB,rb_opC,rb_opD,rbselect,rbcorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice);
        tv_tittle = (TextView)findViewById(R.id.tv_tittle);
        tv_score = (TextView) findViewById(R.id.tv_quessno);
        tv_quess = (TextView) findViewById(R.id.tv_quess);
        tv_correct = (TextView) findViewById(R.id.tv_correct);
        bt_next = (Button) findViewById(R.id.bt_nextquess);
        rb_opA=(RadioButton) findViewById(R.id.rd_option1);
        rb_opB=(RadioButton) findViewById(R.id.rd_option2);
        rb_opC=(RadioButton) findViewById(R.id.rd_option3);
        rb_opD=(RadioButton) findViewById(R.id.rd_option4);
        rd_grp = (RadioGroup) findViewById(R.id.radioGroup);

        Intent i = getIntent();
        tittle = i.getStringExtra("Sub");
        lang = i.getStringExtra("lang");

        rd_grp.setOnCheckedChangeListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        m_db = FirebaseDatabase.getInstance().getReference();

        tv_tittle.setText(tittle);
        qno_list = String.format("%03d", qno);
        getQuestion(qno_list);

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rd_grp.clearCheck();
               // checkedRadioButtonID = rd_grp.getCheckedRadioButtonId();
                rd_grp.setOnCheckedChangeListener(MultipleChoiceActivity.this);
                qno++;
                qno_list = String.format("%03d", qno);
                getQuestion(qno_list);

            }
        });
    }

    private void getQuestion(final String qno_list) {
        rb_opA.setClickable(true);
        rb_opB.setClickable(true);
        rb_opC.setClickable(true);
        rb_opD.setClickable(true);

        rb_opA.setTextColor(ContextCompat.getColor(MultipleChoiceActivity.this, R.color.black));
        rb_opB.setTextColor(ContextCompat.getColor(MultipleChoiceActivity.this, R.color.black));
        rb_opC.setTextColor(ContextCompat.getColor(MultipleChoiceActivity.this, R.color.black));
        rb_opD.setTextColor(ContextCompat.getColor(MultipleChoiceActivity.this, R.color.black));


        Query querry_totalquess = m_db.child(lang).child("subjectList").child(tittle).orderByKey();
        // Log.i("datasnapshot79",""+querry_totalquess.getRef());

        querry_totalquess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String total = String.valueOf(dataSnapshot.getChildrenCount());
                String quess_no = String.valueOf(qno);
                if (qno <= dataSnapshot.getChildrenCount()) {
                    tv_score.setText(quess_no + "/" + total);
                }
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        // Log.i("datasnapshot77",""+data.getKey());
                        //Log.i("datasnapshot78",""+data.getValue());
                        String keydata = "Q-" + qno_list;
                        if (data.getKey().equals(keydata)) {
                            // Log.i("datasnapshot77",""+data.getKey());
                            // Log.i("datasnapshot78",""+data.getValue());
                            QuestionModel qModel = data.getValue(QuestionModel.class);

                            // Log.i("datasnapshot76",""+qModel.getAnswer());
                            // Log.i("datasnapshot75",""+qModel.getQuestion());
                            tv_quess.setText(qModel.getQuestion());
                            rb_opA.setText(qModel.getOptionA());
                            rb_opB.setText(qModel.getOptionB());
                            rb_opC.setText(qModel.getOptionC());
                            rb_opD.setText(qModel.getOptionD());
                            correct_ans = qModel.getCorrect();

                           // tv_ans.setText("Ans : " +    qModel.getAnswer());
                        }


                    }


                } else {
                    Toast.makeText(MultipleChoiceActivity.this, "There is no Questions", Toast.LENGTH_SHORT).show();


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

  /*

     End
         */



    }



    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

        rbselect = (RadioButton)radioGroup.findViewById(i);
        rbcorrect= (RadioButton)radioGroup.findViewById(i);

        switch (i) {
            case R.id.rd_option1:
                selectedAns = rb_opA.getText().toString();
                break;
            case R.id.rd_option2:
                selectedAns = rb_opB.getText().toString();
                break;
            case R.id.rd_option3:
                selectedAns = rb_opC.getText().toString();
                break;
            case R.id.rd_option4:
                selectedAns = rb_opD.getText().toString();
                break;

        }
        if(rbselect != null){
            if (selectedAns.equals(correct_ans)) {

                rbselect.setTextColor(ContextCompat.getColor(MultipleChoiceActivity.this, R.color.green));
                rbcorrect = rbselect;
                rb_opA.setClickable(false);
                rb_opB.setClickable(false);
                rb_opC.setClickable(false);
                rb_opD.setClickable(false);


            } else {

                rbselect.setTextColor(ContextCompat.getColor(MultipleChoiceActivity.this, R.color.red));
                if (rb_opA.getText().toString().equals(correct_ans)) {
                    rb_opA.setTextColor(ContextCompat.getColor(MultipleChoiceActivity.this, R.color.green));
                    rbcorrect = rb_opA;
                } else if (rb_opB.getText().toString().equals(correct_ans)) {
                    rb_opB.setTextColor(ContextCompat.getColor(MultipleChoiceActivity.this, R.color.green));
                    rbcorrect = rb_opB;
                } else if (rb_opC.getText().toString().equals(correct_ans)) {
                    rb_opC.setTextColor(ContextCompat.getColor(MultipleChoiceActivity.this, R.color.green));
                    rbcorrect = rb_opC;
                } else if (rb_opD.getText().toString().equals(correct_ans)) {
                    rb_opD.setTextColor(ContextCompat.getColor(MultipleChoiceActivity.this, R.color.green));
                    rbcorrect = rb_opD;
                }
                rb_opA.setClickable(false);
                rb_opB.setClickable(false);
                rb_opC.setClickable(false);
                rb_opD.setClickable(false);


            }
        }

    }
}