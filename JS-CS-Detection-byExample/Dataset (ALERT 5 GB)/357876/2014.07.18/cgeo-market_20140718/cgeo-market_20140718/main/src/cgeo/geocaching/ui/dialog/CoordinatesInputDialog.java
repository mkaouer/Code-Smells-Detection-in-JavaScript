package cgeo.geocaching.ui.dialog;

import cgeo.geocaching.Geocache;
import cgeo.geocaching.sensors.IGeoData;
import cgeo.geocaching.R;
import cgeo.geocaching.activity.AbstractActivity;
import cgeo.geocaching.activity.ActivityMixin;
import cgeo.geocaching.geopoint.Geopoint;
import cgeo.geocaching.geopoint.GeopointFormatter;
import cgeo.geocaching.settings.Settings;
import cgeo.geocaching.settings.Settings.CoordInputFormatEnum;
import cgeo.geocaching.utils.EditUtils;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CoordinatesInputDialog extends NoTitleDialog {

    final private AbstractActivity context;
    final private IGeoData geo;
    final private Geocache cache;
    private Geopoint gp;

    private EditText eLat, eLon;
    private Button bLat, bLon;
    private EditText eLatDeg, eLatMin, eLatSec, eLatSub;
    private EditText eLonDeg, eLonMin, eLonSec, eLonSub;
    private TextView tLatSep1, tLatSep2, tLatSep3;
    private TextView tLonSep1, tLonSep2, tLonSep3;

    private CoordinateUpdate cuListener;

    private CoordInputFormatEnum currentFormat = null;

    public CoordinatesInputDialog(final AbstractActivity context, final Geocache cache, final Geopoint gp, final IGeoData geo) {
        super(context, ActivityMixin.getDialogTheme());
        this.context = context;
        this.geo = geo;
        this.cache = cache;

        if (gp != null) {
            this.gp = gp;
        } else if (geo != null && geo.getCoords() != null) {
            this.gp = geo.getCoords();
        } else {
            this.gp = Geopoint.ZERO;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coordinatesinput_dialog);

        final Spinner spinner = (Spinner) findViewById(R.id.spinnerCoordinateFormats);
        final ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(context,
                        R.array.waypoint_coordinate_formats,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(Settings.getCoordInputFormat().ordinal());
        spinner.setOnItemSelectedListener(new CoordinateFormatListener());

        bLat = (Button) findViewById(R.id.ButtonLat);
        eLat = (EditText) findViewById(R.id.latitude);
        eLatDeg = (EditText) findViewById(R.id.EditTextLatDeg);
        eLatMin = (EditText) findViewById(R.id.EditTextLatMin);
        eLatSec = (EditText) findViewById(R.id.EditTextLatSec);
        eLatSub = (EditText) findViewById(R.id.EditTextLatSecFrac);
        tLatSep1 = (TextView) findViewById(R.id.LatSeparator1);
        tLatSep2 = (TextView) findViewById(R.id.LatSeparator2);
        tLatSep3 = (TextView) findViewById(R.id.LatSeparator3);

        bLon = (Button) findViewById(R.id.ButtonLon);
        eLon = (EditText) findViewById(R.id.longitude);
        eLonDeg = (EditText) findViewById(R.id.EditTextLonDeg);
        eLonMin = (EditText) findViewById(R.id.EditTextLonMin);
        eLonSec = (EditText) findViewById(R.id.EditTextLonSec);
        eLonSub = (EditText) findViewById(R.id.EditTextLonSecFrac);
        tLonSep1 = (TextView) findViewById(R.id.LonSeparator1);
        tLonSep2 = (TextView) findViewById(R.id.LonSeparator2);
        tLonSep3 = (TextView) findViewById(R.id.LonSeparator3);

        eLatDeg.addTextChangedListener(new TextChanged(eLatDeg));
        eLatMin.addTextChangedListener(new TextChanged(eLatMin));
        eLatSec.addTextChangedListener(new TextChanged(eLatSec));
        eLatSub.addTextChangedListener(new TextChanged(eLatSub));
        eLonDeg.addTextChangedListener(new TextChanged(eLonDeg));
        eLonMin.addTextChangedListener(new TextChanged(eLonMin));
        eLonSec.addTextChangedListener(new TextChanged(eLonSec));
        eLonSub.addTextChangedListener(new TextChanged(eLonSub));

        EditUtils.disableSuggestions(eLatDeg);
        EditUtils.disableSuggestions(eLatMin);
        EditUtils.disableSuggestions(eLatSec);
        EditUtils.disableSuggestions(eLatSub);
        EditUtils.disableSuggestions(eLonDeg);
        EditUtils.disableSuggestions(eLonMin);
        EditUtils.disableSuggestions(eLonSec);
        EditUtils.disableSuggestions(eLonSub);

        bLat.setOnClickListener(new ButtonClickListener());
        bLon.setOnClickListener(new ButtonClickListener());

        final Button buttonCurrent = (Button) findViewById(R.id.current);
        buttonCurrent.setOnClickListener(new CurrentListener());
        final Button buttonCache = (Button) findViewById(R.id.cache);
        if (cache != null) {
            buttonCache.setOnClickListener(new CacheListener());
        } else {
            buttonCache.setVisibility(View.GONE);
        }
        final Button buttonDone = (Button) findViewById(R.id.done);
        buttonDone.setOnClickListener(new InputDoneListener());
    }

    private void updateGUI() {
        if (gp == null) {
            return;
        }

        bLat.setText(String.valueOf(gp.getLatDir()));
        bLon.setText(String.valueOf(gp.getLonDir()));

        switch (currentFormat) {
            case Plain:
                findViewById(R.id.coordTable).setVisibility(View.GONE);
                eLat.setVisibility(View.VISIBLE);
                eLon.setVisibility(View.VISIBLE);
                eLat.setText(gp.format(GeopointFormatter.Format.LAT_DECMINUTE));
                eLon.setText(gp.format(GeopointFormatter.Format.LON_DECMINUTE));
                break;
            case Deg: // DDD.DDDDD°
                findViewById(R.id.coordTable).setVisibility(View.VISIBLE);
                eLat.setVisibility(View.GONE);
                eLon.setVisibility(View.GONE);
                eLatSec.setVisibility(View.GONE);
                eLonSec.setVisibility(View.GONE);
                tLatSep3.setVisibility(View.GONE);
                tLonSep3.setVisibility(View.GONE);
                eLatSub.setVisibility(View.GONE);
                eLonSub.setVisibility(View.GONE);

                tLatSep1.setText(".");
                tLonSep1.setText(".");
                tLatSep2.setText("°");
                tLonSep2.setText("°");

                eLatDeg.setText(addZeros(gp.getLatDeg(), 2));
                eLatMin.setText(addZeros(gp.getLatDegFrac(), 5));
                eLonDeg.setText(addZeros(gp.getLonDeg(), 3));
                eLonMin.setText(addZeros(gp.getLonDegFrac(), 5));
                break;
            case Min: // DDD° MM.MMM
                findViewById(R.id.coordTable).setVisibility(View.VISIBLE);
                eLat.setVisibility(View.GONE);
                eLon.setVisibility(View.GONE);
                eLatSec.setVisibility(View.VISIBLE);
                eLonSec.setVisibility(View.VISIBLE);
                tLatSep3.setVisibility(View.VISIBLE);
                tLonSep3.setVisibility(View.VISIBLE);
                eLatSub.setVisibility(View.GONE);
                eLonSub.setVisibility(View.GONE);

                tLatSep1.setText("°");
                tLonSep1.setText("°");
                tLatSep2.setText(".");
                tLonSep2.setText(".");
                tLatSep3.setText("'");
                tLonSep3.setText("'");

                eLatDeg.setText(addZeros(gp.getLatDeg(), 2));
                eLatMin.setText(addZeros(gp.getLatMin(), 2));
                eLatSec.setText(addZeros(gp.getLatMinFrac(), 3));
                eLonDeg.setText(addZeros(gp.getLonDeg(), 3));
                eLonMin.setText(addZeros(gp.getLonMin(), 2));
                eLonSec.setText(addZeros(gp.getLonMinFrac(), 3));
                break;
            case Sec: // DDD° MM SS.SSS
                findViewById(R.id.coordTable).setVisibility(View.VISIBLE);
                eLat.setVisibility(View.GONE);
                eLon.setVisibility(View.GONE);
                eLatSec.setVisibility(View.VISIBLE);
                eLonSec.setVisibility(View.VISIBLE);
                tLatSep3.setVisibility(View.VISIBLE);
                tLonSep3.setVisibility(View.VISIBLE);
                eLatSub.setVisibility(View.VISIBLE);
                eLonSub.setVisibility(View.VISIBLE);

                tLatSep1.setText("°");
                tLonSep1.setText("°");
                tLatSep2.setText("'");
                tLonSep2.setText("'");
                tLatSep3.setText(".");
                tLonSep3.setText(".");

                eLatDeg.setText(addZeros(gp.getLatDeg(), 2));
                eLatMin.setText(addZeros(gp.getLatMin(), 2));
                eLatSec.setText(addZeros(gp.getLatSec(), 2));
                eLatSub.setText(addZeros(gp.getLatSecFrac(), 3));
                eLonDeg.setText(addZeros(gp.getLonDeg(), 3));
                eLonMin.setText(addZeros(gp.getLonMin(), 2));
                eLonSec.setText(addZeros(gp.getLonSec(), 2));
                eLonSub.setText(addZeros(gp.getLonSecFrac(), 3));
                break;
        }
    }

    private static String addZeros(final int value, final int len) {
        return StringUtils.leftPad(Integer.toString(value), len, '0');
    }

    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            assert view instanceof Button;
            final Button button = (Button) view;
            final CharSequence text = button.getText();
            if (StringUtils.isBlank(text)) {
                return;
            }
            switch (text.charAt(0)) {
                case 'N':
                    button.setText("S");
                    break;
                case 'S':
                    button.setText("N");
                    break;
                case 'E':
                    button.setText("W");
                    break;
                case 'W':
                    button.setText("E");
                    break;
                default:
                    break;
            }

            // This will serve as a reminder to the user that the current coordinates might not be valid
            areCurrentCoordinatesValid(true);
        }
    }

    private class TextChanged implements TextWatcher {

        private final EditText editText;

        public TextChanged(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void afterTextChanged(Editable s) {
            /*
             * Max lengths, depending on currentFormat
             *
             * formatPlain = disabled
             * DEG MIN SEC SUB
             * formatDeg 2/3 5 - -
             * formatMin 2/3 2 3 -
             * formatSec 2/3 2 2 3
             */

            if (currentFormat == CoordInputFormatEnum.Plain) {
                return;
            }

            final int maxLength = getMaxLengthFromCurrentField(editText);
            if (s.length() == maxLength) {
                if (editText == eLatDeg) {
                    eLatMin.requestFocus();
                } else if (editText == eLatMin) {
                    if (eLatSec.getVisibility() == View.GONE) {
                        eLonDeg.requestFocus();
                    } else {
                        eLatSec.requestFocus();
                    }
                } else if (editText == eLatSec) {
                    if (eLatSub.getVisibility() == View.GONE) {
                        eLonDeg.requestFocus();
                    } else {
                        eLatSub.requestFocus();
                    }
                } else if (editText == eLatSub) {
                    eLonDeg.requestFocus();
                } else if (editText == eLonDeg) {
                    eLonMin.requestFocus();
                } else if (editText == eLonMin) {
                    if (eLonSec.getVisibility() == View.GONE) {
                        eLatDeg.requestFocus();
                    } else {
                        eLonSec.requestFocus();
                    }
                } else if (editText == eLonSec) {
                    if (eLonSub.getVisibility() == View.GONE) {
                        eLatDeg.requestFocus();
                    } else {
                        eLonSub.requestFocus();
                    }
                } else if (editText == eLonSub) {
                    eLatDeg.requestFocus();
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

    }

    private boolean areCurrentCoordinatesValid(final boolean signalError) {
        try {
            Geopoint current = null;
            if (currentFormat == CoordInputFormatEnum.Plain) {
                current = new Geopoint(eLat.getText().toString(), eLon.getText().toString());
            } else {
                final String latDir = bLat.getText().toString();
                final String lonDir = bLon.getText().toString();
                final String latDeg = eLatDeg.getText().toString();
                final String lonDeg = eLonDeg.getText().toString();
                final String latDegFrac = eLatMin.getText().toString();
                final String lonDegFrac = eLonMin.getText().toString();
                final String latMin = eLatMin.getText().toString();
                final String lonMin = eLonMin.getText().toString();
                final String latMinFrac = eLatSec.getText().toString();
                final String lonMinFrac = eLonSec.getText().toString();
                final String latSec = eLatSec.getText().toString();
                final String lonSec = eLonSec.getText().toString();
                final String latSecFrac = eLatSub.getText().toString();
                final String lonSecFrac = eLonSub.getText().toString();

                switch (currentFormat) {
                    case Deg:
                        current = new Geopoint(latDir, latDeg, latDegFrac, lonDir, lonDeg, lonDegFrac);
                        break;
                    case Min:
                        current = new Geopoint(latDir, latDeg, latMin, latMinFrac, lonDir, lonDeg, lonMin, lonMinFrac);
                        break;
                    case Sec:
                        current = new Geopoint(latDir, latDeg, latMin, latSec, latSecFrac, lonDir, lonDeg, lonMin, lonSec, lonSecFrac);
                        break;
                    case Plain:
                        // This case has been handled above
                }
            }
            // The null check is necessary to keep FindBugs happy
            if (current != null && current.isValid()) {
                gp = current;
                return true;
            }
        } catch (final Geopoint.ParseException e) {
            // Signaled and returned below
        }
        if (signalError) {
            context.showToast(context.getResources().getString(R.string.err_parse_lat_lon));
        }
        return false;
    }

    public int getMaxLengthFromCurrentField(final EditText editText) {
        if (editText == eLonDeg || editText == eLatSub || editText == eLonSub) {
            return 3;
        }
        if ((editText == eLatMin || editText == eLonMin) && currentFormat == CoordInputFormatEnum.Deg) {
            return 5;
        }
        if ((editText == eLatSec || editText == eLonSec) && currentFormat == CoordInputFormatEnum.Min) {
            return 3;
        }
        return 2;
    }

    private class CoordinateFormatListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            // Ignore first call, which comes from onCreate()
            if (currentFormat != null) {

                // Start new format with an acceptable value: either the current one
                // entered by the user, else our current coordinates, else (0,0).
                if (!areCurrentCoordinatesValid(false)) {
                    if (geo != null && geo.getCoords() != null) {
                        gp = geo.getCoords();
                    } else {
                        gp = Geopoint.ZERO;
                    }
                }
            }

            currentFormat = CoordInputFormatEnum.fromInt(pos);
            Settings.setCoordInputFormat(currentFormat);
            updateGUI();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }

    private class CurrentListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (geo == null || geo.getCoords() == null) {
                context.showToast(context.getResources().getString(R.string.err_point_unknown_position));
                return;
            }

            gp = geo.getCoords();
            updateGUI();
        }
    }

    private class CacheListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (cache == null || cache.getCoords() == null) {
                context.showToast(context.getResources().getString(R.string.err_location_unknown));
                return;
            }

            gp = cache.getCoords();
            updateGUI();
        }
    }

    private class InputDoneListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!areCurrentCoordinatesValid(true)) {
                return;
            }
            if (gp != null) {
                cuListener.update(gp);
            }
            dismiss();
        }
    }

    public void setOnCoordinateUpdate(CoordinateUpdate cu) {
        cuListener = cu;
    }

    public interface CoordinateUpdate {
        public void update(final Geopoint gp);
    }

}
