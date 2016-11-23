package cgeo.geocaching.files;

import cgeo.geocaching.Intents;
import cgeo.geocaching.R;
import cgeo.geocaching.activity.AbstractListActivity;
import cgeo.geocaching.activity.ActivityMixin;

import org.apache.commons.lang3.StringUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Dialog for choosing a file or directory.
 */
public class SimpleDirChooser extends AbstractListActivity {
    public static final String EXTRA_CHOOSE_FOR_WRITING = "chooseForWriting";
    private static final String PARENT_DIR = "..        ";
    private File currentDir;
    private FileArrayAdapter adapter;
    private Button okButton = null;
    private int lastPosition = -1;
    private boolean chooseForWriting = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();
        currentDir = dirContaining(extras.getString(Intents.EXTRA_START_DIR));
        chooseForWriting = extras.getBoolean(SimpleDirChooser.EXTRA_CHOOSE_FOR_WRITING, false);

        ActivityMixin.setTheme(this);
        setContentView(R.layout.simple_dir_chooser);

        fill(currentDir);

        okButton = (Button) findViewById(R.id.simple_dir_chooser_ok);
        resetOkButton();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent()
                        .setData(Uri.fromFile(new File(currentDir, adapter.getItem(lastPosition).getName()))));
                finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.simple_dir_chooser_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        EditText pathField = (EditText) findViewById(R.id.simple_dir_chooser_path);
        pathField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPath();
            }

        });
    }

    public void editPath() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.simple_dir_chooser_current_path);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentDir.getPath());
        builder.setView(input);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pathText = input.getText().toString();
                File newPathDir = new File(pathText);
                if (newPathDir.exists() && newPathDir.isDirectory()) {
                    currentDir = newPathDir;
                    fill(currentDir);
                } else {
                    showToast(SimpleDirChooser.this.getResources().getString(R.string.simple_dir_chooser_invalid_path));
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Return the directory containing a given path, or a sensible default.
     *
     * @param path the path to get the enclosing directory from, can be null or empty
     * @return the directory containing <code>path</code>, or a sensible default if none
     */
    private static File dirContaining(final String path) {
        return StringUtils.contains(path, File.separatorChar) ?
                new File(StringUtils.substringBeforeLast(path, Character.toString(File.separatorChar))) :
                Environment.getExternalStorageDirectory();
    }

    private void fill(File dir) {
        lastPosition = -1;
        resetOkButton();
        EditText path = (EditText) findViewById(R.id.simple_dir_chooser_path);
        path.setText(this.getResources().getString(R.string.simple_dir_chooser_current_path) + " " + dir.getAbsolutePath());
        final File[] dirs = dir.listFiles(new DirOnlyFilenameFilter());
        List<Option> listDirs = new ArrayList<Option>();
        try {
            for (File currentDir : dirs) {
                listDirs.add(new Option(currentDir.getName(), currentDir.getAbsolutePath(), currentDir.canWrite()));
            }
        } catch (RuntimeException e) {
        }
        Collections.sort(listDirs, Option.NAME_COMPARATOR);
        if (dir.getParent() != null) {
            listDirs.add(0, new Option(PARENT_DIR, dir.getParent(), false));
        }
        this.adapter = new FileArrayAdapter(this, R.layout.simple_dir_item, listDirs);
        this.setListAdapter(adapter);
    }

    private void resetOkButton() {
        if (okButton != null) {
            okButton.setEnabled(false);
            okButton.setVisibility(View.INVISIBLE);
        }
    }

    public class FileArrayAdapter extends ArrayAdapter<Option> {

        private Context context;
        private int id;
        private List<Option> items;

        public FileArrayAdapter(Context context, int simpleDirItemResId, List<Option> objects) {
            super(context, simpleDirItemResId, objects);
            this.context = context;
            this.id = simpleDirItemResId;
            this.items = objects;
        }

        @Override
        public Option getItem(int index) {
            return items.get(index);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(id, null);
            }

            final Option option = items.get(position);
            if (option != null) {
                TextView t1 = (TextView) v.findViewById(R.id.TextView01);
                if (t1 != null) {
                    t1.setOnClickListener(new OnTextViewClickListener(position));
                    t1.setText(option.getName());
                }
                CheckBox check = (CheckBox) v.findViewById(R.id.CheckBox);
                if (check != null) {
                    if (!chooseForWriting || option.isWriteable()) {
                        check.setOnClickListener(new OnCheckBoxClickListener(position));
                        check.setChecked(option.isChecked());
                        check.setEnabled(true);
                    } else {
                        check.setEnabled(false);
                    }
                }
            }
            return v;
        }
    }

    public class OnTextViewClickListener implements OnClickListener {
        private int position;

        OnTextViewClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View arg0) {
            Option option = adapter.getItem(position);
            if (option.getName().equals(PARENT_DIR)) {
                currentDir = new File(option.getPath());
                fill(currentDir);
            } else {
                File dir = new File(option.getPath());
                if (dir.list(new DirOnlyFilenameFilter()).length > 0) {
                    currentDir = dir;
                    fill(currentDir);
                }
            }
        }
    }

    public class OnCheckBoxClickListener implements OnClickListener {
        private int position;

        OnCheckBoxClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View arg0) {
            Option lastOption = (lastPosition > -1) ? adapter.getItem(lastPosition) : null;
            Option currentOption = adapter.getItem(position);
            if (lastOption != null) {
                lastOption.setChecked(false);
            }
            if (currentOption != lastOption) {
                currentOption.setChecked(true);
                lastPosition = position;
            } else {
                lastPosition = -1;
            }
            final boolean enabled = currentOption.isChecked() && !currentOption.getName().equals(PARENT_DIR);
            okButton.setEnabled(enabled);
            okButton.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    public static class Option {
        private final String name;
        private final String path;
        private boolean checked = false;
        private boolean writeable = false;

        private static Comparator<Option> NAME_COMPARATOR = new Comparator<SimpleDirChooser.Option>() {

            @Override
            public int compare(Option lhs, Option rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.name, rhs.name);
            }

        };

        public Option(String name, String path, boolean writeable) {
            this.name = name;
            this.path = path;
            this.writeable = writeable;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public boolean isChecked() {
            return this.checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public boolean isWriteable() {
            return writeable;
        }
    }

    public static class DirOnlyFilenameFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String filename) {
            File file = new File(dir, filename);
            return file.isDirectory() && file.canRead();
        }

    }
}
