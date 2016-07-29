package hiwi.mike.auftraganalyseapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import hiwi.mike.auftraganalyseapp.CursorAdapter.ExportExpandableListAdapater;
import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class NewExportActivity extends AppCompatActivity {

    ExportExpandableListAdapater expandableListAdapater;
    ExpandableListView           expandableListView;

    private Menu menu;

    Set<Integer> selectedWorkbooks;
    Set<Integer>  selectedProjects;
    Set<Integer>  activelyDeselectedProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_export);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_export);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportToXSL(
                        selectedWorkbooks.toArray(new Integer[selectedWorkbooks.size()]),
                        selectedProjects.toArray(new Integer[selectedProjects.size()]), false);
                finish();
            }

        });

        selectedWorkbooks = new HashSet<>();
        selectedProjects = new HashSet<>();
        activelyDeselectedProjects = new HashSet<>();


        WorkbookDbHelper dbHelper = new WorkbookDbHelper(this);
        SQLiteDatabase sqlDB = dbHelper.getReadableDatabase();



        expandableListView = (ExpandableListView) findViewById(R.id.exportLV);

        expandableListAdapater = new ExportExpandableListAdapater(getBaseContext(), sqlDB);

        expandableListView.setAdapter(expandableListAdapater);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                ImageView iv = (ImageView) v.findViewById(R.id.elCheck);
                Integer wbID = ((Pair<Integer,String>)expandableListAdapater.getGroup(groupPosition)).first;

                if (iv.getVisibility() == View.VISIBLE)
                {
                    iv.setVisibility(View.INVISIBLE);

                    selectedWorkbooks.remove(wbID);

                    for (int i = 0; i < expandableListAdapater.getChildrenCount(groupPosition); i++)
                    {
                        Integer prjID = ((Pair<Integer,String>)expandableListAdapater.getChild(groupPosition, i)).first;
                        selectedProjects.remove(prjID);
                    }

                } else
                {
                    iv.setVisibility(View.VISIBLE);

                    selectedWorkbooks.add(wbID);
                    for (int i = 0; i < expandableListAdapater.getChildrenCount(groupPosition); i++)
                    {
                        Integer prjID = ((Pair<Integer,String>)expandableListAdapater.getChild(groupPosition, i)).first;
                        if (!activelyDeselectedProjects.contains(prjID))
                        {
                            selectedProjects.add(prjID);
                        }
                    }

                }
                Log.d("GroupClick SelectedWB", selectedWorkbooks.toString());
                Log.d("GroupClick SelectedPrj", selectedProjects.toString());
                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ImageView iv = (ImageView) v.findViewById(R.id.elCheck);
                Integer prjID = ((Pair<Integer,String>)expandableListAdapater.getChild(groupPosition, childPosition)).first;

                if (iv.getVisibility() == View.VISIBLE)
                {
                    iv.setVisibility(View.INVISIBLE);

                    selectedProjects.remove(prjID);
                    activelyDeselectedProjects.add(prjID);

                } else
                {
                    iv.setVisibility(View.VISIBLE);

                    selectedProjects.add(prjID);
                    activelyDeselectedProjects.remove(prjID);
                }
                Log.d("GroupClick SelectedWB", selectedWorkbooks.toString());
                Log.d("GroupClick SelectedPrj", selectedProjects.toString());
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportToXSL(Integer[] workbooks, Integer[] projects, boolean override) {
        if (!(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())))
        {
            Log.d("export XSL", "no external Staorage");
            return;
        }


        WorkbookDbHelper dbHelper = new WorkbookDbHelper(this);
        SQLiteDatabase sqlDB = dbHelper.getReadableDatabase();
        Cursor crs;
        WorkbookSettings wbSettings = new WorkbookSettings();
        WritableFont arialFont = new WritableFont(WritableFont.ARIAL, 10);
        WritableCellFormat arialFormat = new WritableCellFormat(arialFont);
        try {
            arialFormat.setWrap(true);
        } catch (WriteException e) {
            e.printStackTrace();
        }
        WritableFont arialBoldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        WritableCellFormat arialBoldFormat = new WritableCellFormat(arialBoldFont);
        try {
            arialBoldFormat.setWrap(true);
        } catch (WriteException e) {
            e.printStackTrace();
        }

        final String fExtension = ".xls";

        wbSettings.setLocale(new Locale("de", "DE"));

        // Create a new Excel Workbook for each Workbook
        for (int i = 0; i < workbooks.length; ++i) {
            String workbookName;
            File file;

            crs = sqlDB.rawQuery(WorkbookContract.GET_WORKBOOKS_BY_ID(workbooks[i]), null);
            crs.moveToFirst();
            workbookName = crs.getString(crs.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME));
            crs.close();
            file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                            "/" + getText(R.string.app_name).toString());

            if (!file.mkdirs())
            {
                Log.e("export xls", "Directory not created");
                //return;
            }

            file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                            "/" + getText(R.string.app_name).toString(), workbookName + fExtension);

            if (file.exists()) {
                if (!override) {
                    int a = 0;
                    while (file.exists()) {
                        file = new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                                        "/" + getText(R.string.app_name).toString(), workbookName + ++a + fExtension);
                    }
                } else if (file.isDirectory()) {
                    int a = 0;
                    while (file.isDirectory()) {
                        file = new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                                        "/" + getText(R.string.app_name).toString(), workbookName + ++a + fExtension);
                    }
                }
            }


            WritableWorkbook workbook = null;
            try {
                workbook = Workbook.createWorkbook(file, wbSettings);
            } catch (Throwable e) {
                Log.e("export", e.toString(), e);
            }

            // Insert all Projects as Worksheets into the Workbook
            for (int y = 0; y < projects.length; ++y) {
                String worksheetName;
                WritableSheet worksheet;

                crs = sqlDB.rawQuery(WorkbookContract.GET_PROJECTS_BY_ID(projects[y]), null);
                crs.moveToFirst();

                if (crs.getInt(crs.getColumnIndexOrThrow(
                        WorkbookContract.ProjectEntry.COLUMN_NAME_WORKBOOK_ID)) == workbooks[i]) {
                    worksheetName = crs.getString(crs.getColumnIndexOrThrow(
                            WorkbookContract.ProjectEntry.COLUMN_NAME_ENTRY_NAME));
                    worksheet = workbook.createSheet(worksheetName, Integer.MAX_VALUE);

                    // Add caption
                    try {
                        worksheet.addCell(new Label(0, 0, "#", arialBoldFormat));
                        worksheet.addCell(new Label(1, 0, "Zieldatum", arialBoldFormat));
                        worksheet.addCell(new Label(2, 0, "ZAU", arialBoldFormat));
                        worksheet.addCell(new Label(3, 0, "WIP", arialBoldFormat));
                        worksheet.addCell(new Label(4, 0, "Arbeitsstation", arialBoldFormat));
                    }catch (WriteException e)
                    {
                        e.printStackTrace();
                    }
                    crs = sqlDB.rawQuery(WorkbookContract.GET_ORDERS_BY_PROJECT(projects[y]),null);
                    crs.moveToFirst();
                    int a = 1;
                    while (!crs.isAfterLast()) {
                        try {
                            worksheet.addCell(new Label(0, a,
                                    crs.getString(crs.getColumnIndexOrThrow(
                                            WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR)),
                                    arialFormat));
                            worksheet.addCell(new Label(1, a,
                                    crs.getString(crs.getColumnIndexOrThrow(
                                            WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE)),
                                    arialFormat));

                            worksheet.addCell(new Label(2, a,
                                    crs.getString(crs.getColumnIndexOrThrow(
                                            WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TIME)),
                                    arialFormat));
                            worksheet.addCell(new Number(3, a,
                                    crs.getInt(crs.getColumnIndexOrThrow(
                                            WorkbookContract.OrderEntry.COLUMN_NAME_WIP)),
                                    arialFormat));
                            worksheet.addCell(new Label(4, a,
                                    crs.getString(crs.getColumnIndexOrThrow(
                                            WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME)),
                                    arialFormat));
                        } catch (WriteException e) {
                            e.printStackTrace();
                        }
                        a++;
                        crs.moveToNext();
                    }
                    crs.close();
                }
            }

            try {
                assert workbook != null;
                workbook.write();
                workbook.close();
            } catch (Exception e) {
                Log.e("export", e.toString(), e);
            }
        }
    }
}