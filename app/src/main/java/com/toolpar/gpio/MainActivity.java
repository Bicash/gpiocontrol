package com.toolpar.gpio;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.Objects;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    RadioGroup out_in;
    Button Create, Clear;
    int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
    private static final String TAG = MainActivity.class.getSimpleName();
    String[] data = {"BCM2", "BCM3", "BCM7", "BCM8", "BCM9", "BCM10", "BCM11", "BCM13", "BCM14", "BCM15", "BCM18", "BCM19", "BCM20", "BCM21"};
    //private Gpio mLedGpio, inled;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout createe = (LinearLayout) findViewById(R.id.creatinggpio);
        out_in = (RadioGroup) findViewById(R.id.out_in);
        Create = (Button) findViewById(R.id.create);
        Clear = (Button) findViewById(R.id.clear);
        /////////////////////////////////////////////////////////////////////
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = (Spinner) findViewById(R.id.gpio);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Title");
        // выделяем элемент
        spinner.setSelection(2);
        // устанавливаем обработчик нажатия
        final PeripheralManagerService service = new PeripheralManagerService();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        Create.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(wrapContent, wrapContent);
                        String btnout_in = "output";
                        switch (out_in.getCheckedRadioButtonId()) {
                            case R.id.inputset:
                                btnout_in = "input";
                                break;
                            case R.id.outputset:
                                btnout_in = "output";
                                break;
                        }
                        if (Objects.equals(btnout_in, "output")) {
                            String selected = spinner.getSelectedItem().toString();

                            Button btnNew = new Button(MainActivity.this);
                            btnNew.setText(selected);
                            createe.addView(btnNew, lparams);
                            try {
                                final Gpio mLedGpio = service.openGpio(selected);
                                mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                                btnNew.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                mLedGpio.setValue(!mLedGpio.getValue());
                                                Log.d(TAG, "DONE: " + mLedGpio.getName() + " - "+ mLedGpio.getValue());
                                            } catch (IOException e) {
                                                Log.e(TAG, "Error on PeripheralIO API", e);
                                            }
                                        }
                                    }
                                );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (Objects.equals(btnout_in, "input")) {
                            String selected = spinner.getSelectedItem().toString();
                            TextView textNew = new TextView(MainActivity.this);
                            textNew.setText(selected);
                            createe.addView(textNew, lparams);
                            try {
                                final Gpio inGpio = service.openGpio(selected);
                                textNew.setText(textNew.getText()+ String.valueOf(inGpio.getValue()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        Clear.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createe.removeAllViews();
                        Toast.makeText(MainActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
