package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.Helper.Helper;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 16.06.16.
 */
public class EditWorkstationDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private Runnable onCleanup;
    private int      workstation_id;
    private String   name;
    private Double   output;
    private String   reihenfolge;

    private EditText inp_name;
    private EditText inp_output;
    private EditText inp_reihenfolge_other;
    private Spinner  inp_reihenfolge_spinner;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Context context = getActivity();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.editworkstationdialog, null);

        inp_name = (EditText) layout.findViewById(R.id.name);
        inp_output = (EditText) layout.findViewById(R.id.output);
        inp_reihenfolge_other = (EditText) layout.findViewById(R.id.reihenfolge_other);
        inp_reihenfolge_spinner = (Spinner) layout.findViewById(R.id.reihenfolge_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.reihenfolge_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        inp_reihenfolge_spinner.setAdapter(adapter);
        inp_reihenfolge_spinner.setOnItemSelectedListener(this);
        if (reihenfolge != null) {
            int pos = Helper.searchSpinnerForValue(inp_reihenfolge_spinner, reihenfolge);
            if (pos >= 0) {
                inp_reihenfolge_spinner.setSelection(pos);
            }
            else {
                inp_reihenfolge_spinner.setSelection(Helper.searchSpinnerForValue(inp_reihenfolge_spinner, "andere:"));
                inp_reihenfolge_other.setText(reihenfolge);
            }
        } else
        {
            inp_reihenfolge_spinner.setSelection(getResources().getStringArray(R.array.reihenfolge_array).length - 1);
        }
        inp_name.setText(name);
        inp_output.setText(output.toString());

        builder.setMessage("Bearbeite Maschine/Arbeitsplatz")
                .setView(layout)
                .setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());
                        if (inp_name.getText().length() > 0 && inp_output.getText().length() > 0) {
                            ContentValues values = new ContentValues();
                            values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME,
                                    inp_name.getText().toString());
                            values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_OUTPUT,
                                    Double.parseDouble(inp_output.getText().toString()));

                            if (inp_reihenfolge_spinner.getSelectedItem().equals("andere:"))
                            {
                                values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_REIHENFOLGE,
                                        inp_reihenfolge_other.toString());
                            }else if (!inp_reihenfolge_spinner.getSelectedItem().equals("nicht definiert"))
                            {
                                values.put(WorkbookContract.WorkstationEntry.COLUMN_NAME_REIHENFOLGE,
                                        (String)inp_reihenfolge_spinner.getSelectedItem());
                            }

                            dbHelper.getWritableDatabase().update(
                                    WorkbookContract.WorkstationEntry.TABLE_NAME,
                                    values,
                                    WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_ID + "= ?",
                                    new String[]{Integer.toString(workstation_id)});
                            onCleanup.run();
                        } else {

                        }
                    }
                })
                .setNegativeButton("Löschen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OnAcceptDialogFragment acceptDiaFragment = new OnAcceptDialogFragment();
                        final WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());

                        acceptDiaFragment.setMessage("Diese Arbeitsstation und alle dazugehörigen Aufträge löschen?");
                        acceptDiaFragment.setOnAccept(new Runnable() {
                            @Override
                            public void run() {
                                dbHelper.getWritableDatabase().delete(WorkbookContract.WorkstationEntry.TABLE_NAME,
                                        WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_ID + "= ?",
                                        new String[]{Integer.toString(workstation_id)});
                            }
                        });
                        acceptDiaFragment.setOnCleanup(new Runnable() {
                            @Override
                            public void run() {
                                onCleanup.run();
                            }
                        });

                        acceptDiaFragment.show(getFragmentManager(), null);
                    }
                });
        return builder.create();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        if (parent.getItemAtPosition(pos).equals("andere:"))
        {
            inp_reihenfolge_other.setVisibility(View.VISIBLE);
        } else
        {
            inp_reihenfolge_other.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setCleanup (Runnable run)
    {
        onCleanup = run;
    }

    public void setWorkstation_id(int workstation_id) {
        this.workstation_id = workstation_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOutput(Double output) {
        this.output = output;
    }

    public void setReihenfolge(String reihenfolge) {
        this.reihenfolge = reihenfolge;
    }
}