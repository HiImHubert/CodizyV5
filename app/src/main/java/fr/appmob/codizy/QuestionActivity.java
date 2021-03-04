package fr.appmob.codizy;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView question, qCount, timer;
    private Button opt1, opt2, opt3, opt4;
    private List<Question> questionList;
    private int questionNum, score;
    private CountDownTimer countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        question = findViewById(R.id.question);
        qCount = findViewById(R.id.question_number);
        timer = findViewById(R.id.countdown);

        opt1 = findViewById(R.id.option1);
        opt2 = findViewById(R.id.option2);
        opt3 = findViewById(R.id.option3);
        opt4 = findViewById(R.id.option4);

        opt1.setOnClickListener(this);
        opt2.setOnClickListener(this);
        opt3.setOnClickListener(this);
        opt4.setOnClickListener(this);

        getQuestionList();
        score = 0;
    }

    private void getQuestionList(){
        questionList = new ArrayList<>();
        questionList.add(new Question("Question 1", "A", "B", "C", "D", 2));
        questionList.add(new Question("Question 2", "B", "B", "D", "A", 2));
        questionList.add(new Question("Question 3", "C", "B", "A", "D", 2));
        questionList.add(new Question("Question 4", "A", "D", "C", "B", 2));
        questionList.add(new Question("Question 5", "C", "D", "A", "D", 2));

        setQuestion();
    }

    private void setQuestion() {
        timer.setText(String.valueOf(10));

        question.setText(questionList.get(0).getQuestion());
        opt1.setText(questionList.get(0).getOpt1());
        opt2.setText(questionList.get(0).getOpt2());
        opt3.setText(questionList.get(0).getOpt3());
        opt4.setText(questionList.get(0).getOpt4());

        qCount.setText(String.valueOf(1) + "/" + String.valueOf(questionList.size()));

        startTimer();
        questionNum = 0;
    }

    private void startTimer() {
        countDown = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished < 10000)
                    timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                changeQuestion();
            }
        };
        countDown.start();
    }

    private void changeQuestion() {
        if(questionNum < questionList.size() - 1){
            questionNum++;
            playAnim(question, 0, 0);
            playAnim(opt1, 0, 1);
            playAnim(opt2, 0, 2);
            playAnim(opt3, 0, 3);
            playAnim(opt4, 0, 4);

            qCount.setText(String.valueOf(questionNum+1) + "/" + String.valueOf(questionList.size()));
            timer.setText(String.valueOf(10));
            startTimer();
        }
        else {
            //Dernière question -> Aller à ScoreActivity
            Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);
            intent.putExtra("SCORE", String.valueOf(score) + "/" + String.valueOf(questionList.size()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //QuestionActivity.this.finish();
        }
    }

    //Animation pour changer la question
    private void playAnim(View view, final int value, int viewNum) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
        .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(value == 0){
                    switch(viewNum){
                        case 0:
                            ((TextView)view).setText(questionList.get(questionNum).getQuestion());
                            break;
                        case 1:
                            ((Button)view).setText(questionList.get(questionNum).getOpt1());
                            break;
                        case 2:
                            ((Button)view).setText(questionList.get(questionNum).getOpt2());
                            break;
                        case 3:
                            ((Button)view).setText(questionList.get(questionNum).getOpt3());
                            break;
                        case 4:
                            ((Button)view).setText(questionList.get(questionNum).getOpt4());
                            break;
                    }

                    if(viewNum != 0){
                        ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#87CEFA")));
                    }

                    playAnim(view, 1, viewNum);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int selectedOption = 0;

        switch(v.getId()) {
            case R.id.option1:
                selectedOption = 1;
                break;
            case R.id.option2:
                selectedOption = 2;
                break;
            case R.id.option3:
                selectedOption = 3;
                break;
            case R.id.option4:
                selectedOption = 4;
                break;
            default:
        }
        countDown.cancel();
        checkAnswer(selectedOption, v);
    }

    private void checkAnswer(int selectedOption, View view) {
        if(selectedOption == questionList.get(questionNum).getCorrectOpt()){
            //Bonne réponse
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score++;
        }
        else {
            // Mauvaise réponse
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.RED));

            switch(questionList.get(questionNum).getCorrectOpt()){
                case 1:
                    opt1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    opt2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    opt3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    opt4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
            }
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        countDown.cancel();
    }
}