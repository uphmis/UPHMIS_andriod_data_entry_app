package org.dhis2.mobile_uphmis.ui.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.dhis2.mobile_uphmis.R;
import org.dhis2.mobile_uphmis.WorkService;
import org.dhis2.mobile_uphmis.io.handlers.DialogHandler;
import org.dhis2.mobile_uphmis.io.holders.DatasetInfoHolder;
import org.dhis2.mobile_uphmis.io.json.JsonHandler;
import org.dhis2.mobile_uphmis.io.json.ParsingException;
import org.dhis2.mobile_uphmis.io.models.Category;
import org.dhis2.mobile_uphmis.io.models.CategoryOption;
import org.dhis2.mobile_uphmis.io.models.Form;
import org.dhis2.mobile_uphmis.io.models.FormOptions;
import org.dhis2.mobile_uphmis.io.models.OrganizationUnit;
import org.dhis2.mobile_uphmis.network.HTTPClient;
import org.dhis2.mobile_uphmis.network.NetworkUtils;
import org.dhis2.mobile_uphmis.network.Response;
import org.dhis2.mobile_uphmis.ui.activities.DataEntryActivity;
import org.dhis2.mobile_uphmis.ui.adapters.PickerAdapter;
import org.dhis2.mobile_uphmis.ui.adapters.PickerAdapter.OnPickerListChangeListener;
import org.dhis2.mobile_uphmis.ui.models.Filter;
import org.dhis2.mobile_uphmis.ui.models.Picker;
import org.dhis2.mobile_uphmis.utils.PrefUtils;
import org.dhis2.mobile_uphmis.utils.TextFileUtils;
import org.dhis2.mobile_uphmis.utils.ToastManager;
import org.dhis2.mobile_uphmis.utils.date.DateHolder;
import org.dhis2.mobile_uphmis.utils.date.PeriodFilterFactory;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;
import static org.dhis2.mobile_uphmis.utils.ViewUtils.perfomInAnimation;
import static org.dhis2.mobile_uphmis.utils.ViewUtils.perfomOutAnimation;

public class AggregateReportFragment extends Fragment {
    private static TextView tv_no_dataSets;
    public static final String TAG = AggregateReportFragment.class.getName();
    public static final int AGGREGATE_REPORT_LOADER_ID = TAG.length();
    // index of pickers in list
    private static final int ORG_UNIT_PICKER_ID = 0;
    private static final int DATASET_PICKER_ID = 1;
    private ProgressBar pgsBar;
    private TextView tv_pb;
    // state keys
    private static final String STATE_PICKERS_ONE = "state:pickersOne";
    private static final String STATE_PICKERS_TWO = "state:pickersTwo";
    private static final String STATE_PICKERS_PERIOD = "state:pickersPeriod";
    private static final String STATE_IS_REFRESHING = "state:isRefreshing";
    private static final int SAVED_OFFLINE_LOADER_ID = 2;
    private static final int SAVED_ONLINE_DATASET_ID = 3;
    public static final String SAVED_ONLINE_ACTION = "savedOnlineAction";
    public static final String SAVED_OFFLINE_ACTION = "savedOfflineAction";
    public static String OU_NAME = "";
    public static String OU_ID = "";
    public static String DS_NAME = "";
    public static String DS_ID = "";
    public static String PERIOD = "";


    // generic picker adapters
    private PickerAdapter pickerAdapterOne;
    private PickerAdapter pickerAdapterTwo;
    private RecyclerView pickerRecyclerViewOne;
    private RecyclerView pickerRecyclerViewTwo;

    // period picker views
    private LinearLayout periodPickerLinearLayout;
    private TextView periodPickerTextView;


    // data entry button
    private LinearLayout dataEntryButton;
    private TextView formTextView;
    private TextView formDescriptionTextView;
    private TextView organisationUnitTextView;
    private ImageView offlineSavedIcon;

    // swipe refresh layout
    private SwipeRefreshLayout swipeRefreshLayout;
    private View stubLayout;
    private View rootView;
    private Bundle savedInstanceState;
    private DatasetInfoHolder currentDataSetInfoHolder;
    public static Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String lang = PrefUtils.getLocale(getContext());

        if (lang != null && lang.equals("hi")) {
            Locale locale = new Locale("hi");
            Locale.setDefault(locale);
            Configuration config = getContext().getResources().getConfiguration();
            config.locale = locale;
            getContext().getResources().updateConfiguration(config,
                    getContext().getResources().getDisplayMetrics());
        }
        activity = getActivity();
        setHasOptionsMenu(true);
        initDatesetSentReceiver();
    }

    private void initDatesetSentReceiver() {
        DatasetSentReceiver datasetSentReceiver = new DatasetSentReceiver();
        getActivity().registerReceiver(datasetSentReceiver,
                new IntentFilter(SAVED_ONLINE_ACTION));
        getActivity().registerReceiver(datasetSentReceiver,
                new IntentFilter(SAVED_OFFLINE_ACTION));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aggregate_report, container, false);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        setupViews(root, savedInstanceState);
        rootView = root;
        this.savedInstanceState = savedInstanceState;
    }

    private void setupViews(View root, Bundle savedInstanceState) {
        setupStubLayout(root);
        setupDataEntryButton(root);
        setupPickerRecyclerViews(root, savedInstanceState);
        setupSwipeRefreshLayout(root, savedInstanceState);

        if (savedInstanceState == null) {
            loadData();
        }
    }

    @Override
    public void onPause() {
        if(getActivity() != null)
        {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(onFormsUpdateListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //setupViews(rootView, savedInstanceState);
        if(getActivity() != null)
        {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(onFormsUpdateListener, new IntentFilter(TAG));
        showOfflineSaved(currentDataSetInfoHolder);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                startUpdate();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideSpinners() {
        periodPickerLinearLayout.setVisibility(View.GONE);
        periodPickerLinearLayout.setTag(null);
        periodPickerTextView.setText(null);

        // clear category pickers
        pickerAdapterTwo.swapData(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (pickerAdapterOne != null) {
            pickerAdapterOne.onSaveInstanceState(STATE_PICKERS_ONE, outState);
        }

        if (pickerAdapterTwo != null) {
            pickerAdapterTwo.onSaveInstanceState(STATE_PICKERS_TWO, outState);
        }

        if (periodPickerLinearLayout != null && periodPickerLinearLayout.getTag() != null) {
            outState.putParcelable(STATE_PICKERS_PERIOD,
                    (DateHolder) periodPickerLinearLayout.getTag());
        }

        if (swipeRefreshLayout != null) {
            outState.putBoolean(STATE_IS_REFRESHING, swipeRefreshLayout.isRefreshing());
        }

        super.onSaveInstanceState(outState);
    }

    private void loadData() {
        getLoaderManager().restartLoader(AGGREGATE_REPORT_LOADER_ID, null,
                pickerLoader).forceLoad();
    }

    private void setupStubLayout(View view) {
        stubLayout = view.findViewById(R.id.pull_to_refresh_stub_screen);
        stubLayout.setVisibility(View.GONE);
    }

    private void setupDataEntryButton(View root) {
        dataEntryButton = (LinearLayout) root.findViewById(R.id.user_data_entry);
        formTextView = (TextView) root.findViewById(R.id.choosen_form);
        formDescriptionTextView = (TextView) root.findViewById(R.id.form_description);
        organisationUnitTextView = (TextView) root.findViewById(R.id.choosen_unit);
        offlineSavedIcon = (ImageView) root.findViewById(R.id.offline_saved_icon);
        dataEntryButton.setVisibility(View.GONE);
        pgsBar = (ProgressBar) root.findViewById(R.id.pBar);
        tv_pb = (TextView) root.findViewById(R.id.tv_pbar);
        tv_no_dataSets = (TextView) root.findViewById(R.id.tv_no_dataSets);
    }

    private void setupPickerRecyclerViews(View root, Bundle savedInstanceState) {
        // setting up period picker
        periodPickerLinearLayout = (LinearLayout) root.findViewById(R.id.linearlayout_picker);
        periodPickerTextView = (TextView) root.findViewById(R.id.textview_picker);

        periodPickerLinearLayout.setVisibility(View.GONE);
        periodPickerLinearLayout.setTag(null);

        ImageView periodPickerImageView = (ImageView) root.findViewById(R.id.imageview_cancel);
        periodPickerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDateSelected(null);
            }
        });

        // picker recycler views
        pickerAdapterOne = new PickerAdapter.Builder()
                .context(getActivity())
                .fragmentManager(getChildFragmentManager())
                .build();
        pickerAdapterTwo = new PickerAdapter.Builder()
                .context(getActivity())
                .fragmentManager(getChildFragmentManager())
                .renderPseudoRoots()
                .build();

        LinearLayoutManager layoutManagerOne = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManagerTwo = new LinearLayoutManager(getActivity());

        layoutManagerOne.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManagerTwo.setOrientation(LinearLayoutManager.VERTICAL);

        pickerRecyclerViewOne = (RecyclerView) root
                .findViewById(R.id.recyclerview_pickers_one);
        pickerRecyclerViewTwo = (RecyclerView) root
                .findViewById(R.id.recyclerview_pickers_two);

        pickerRecyclerViewTwo.setLayoutManager(layoutManagerTwo);
        pickerRecyclerViewOne.setLayoutManager(layoutManagerOne);

        pickerRecyclerViewTwo.setAdapter(pickerAdapterTwo);
        pickerRecyclerViewOne.setAdapter(pickerAdapterOne);


        pickerAdapterOne.setOnPickerListChangeListener(new OnPickerListChangeListener() {
            @Override
            public void onPickerListChanged(List<Picker> pickers) {
                // clear category pickers
                pickerAdapterTwo.swapData(null);
                if (pickers.size() > 0) {
                    handlePeriodPicker((FormOptions) pickers.get(0).getTag());
                } else {
                    Toast.makeText(getActivity(), "No DataSet Assigned", Toast.LENGTH_LONG);
                }

//                AggregateReportFragment.this.onPickerListChanged(pickers);
            }
        });

        pickerAdapterTwo.setOnPickerListChangeListener(new OnPickerListChangeListener() {
            @Override
            public void onPickerListChanged(List<Picker> pickers) {
                AggregateReportFragment.this.onPickerSelected();
            }
        });

        pickerAdapterOne.onRestoreInstanceState(STATE_PICKERS_ONE, savedInstanceState);
        pickerAdapterTwo.onRestoreInstanceState(STATE_PICKERS_TWO, savedInstanceState);

        // restoring state of period picker afterwards
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(STATE_PICKERS_PERIOD)) {
            DateHolder dateHolder = savedInstanceState.getParcelable(STATE_PICKERS_PERIOD);

            if (dateHolder != null) {
                periodPickerLinearLayout.setTag(dateHolder);
                periodPickerTextView.setText("");

                // we need to try to render data entry button
                onPickerSelected();
            }
        }
    }

    private void setupSwipeRefreshLayout(View root, Bundle savedInstanceState) {
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.ptr_layout);
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startUpdate();
            }
        };

        @ColorInt
        int blue = R.color.actionbar_blue;
        swipeRefreshLayout.setOnRefreshListener(listener);
        swipeRefreshLayout.setColorSchemeColors(blue, blue);

        PrefUtils.State datasetState = PrefUtils.getResourceState(
                getActivity(), PrefUtils.Resources.DATASETS);

        boolean isRefreshing = false;
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(STATE_IS_REFRESHING)) {
            isRefreshing = savedInstanceState.getBoolean(STATE_IS_REFRESHING, false);
        }

        if (!swipeRefreshLayout.isRefreshing()) {
            isRefreshing = datasetState == PrefUtils.State.REFRESHING;
        }

        if (!isRefreshing) {
            boolean needsUpdate = datasetState == PrefUtils.State.OUT_OF_DATE;
            boolean isConnectionAvailable = NetworkUtils.checkConnection(getActivity());

            if (needsUpdate && isConnectionAvailable) {
                startUpdate();
            }
        } else {
            showProgressBar();
        }
    }


    private void handlePeriodPicker(final FormOptions options) {
        final String choosePeriodPrompt = getString(R.string.choose_period);
        periodPickerTextView.setText(choosePeriodPrompt);

        periodPickerLinearLayout.setVisibility(View.VISIBLE);
        periodPickerLinearLayout.setTag(null);
        //@Sou open future period
        periodPickerTextView.setText(choosePeriodPrompt);
        periodPickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeriodPicker periodPicker = PeriodPicker
                        .newInstance(
                                choosePeriodPrompt,
                                "Monthly",
                                1,
                                null);
                periodPicker.setOnPeriodClickListener(new PeriodPicker.OnPeriodClickListener() {
                    @Override
                    public void onPeriodClicked(DateHolder dateHolder) {
                        onDateSelected(dateHolder);
                    }
                });
                periodPicker.show(getChildFragmentManager());
            }
        });
    }

    private void onDateSelected(DateHolder dateHolder) {
        String label = getString(R.string.choose_period);
        if (dateHolder != null) {
            label = dateHolder.getLabel();
        }

        periodPickerLinearLayout.setTag(dateHolder);
        periodPickerTextView.setText(label);
        onPickerSelected();
    }

    private void onPickerSelected() {
        DatasetInfoHolder datasetInfoHolder = new DatasetInfoHolder();

        // we need to traverse all views and pick up their states
        List<Picker> pickerListOne = pickerAdapterOne.getData();
        DateHolder pickerPeriodDateHolder = null;

        pickerPeriodDateHolder = (DateHolder) periodPickerLinearLayout.getTag();

        // if we have everything in place, we can show data entry button
        if (pickerPeriodDateHolder == null) {
            handleDataEntryButton(null);
            return;
        }

        // set period to dataSetInfoHolder
        datasetInfoHolder.setPeriod(pickerPeriodDateHolder.getDate());
        datasetInfoHolder.setPeriodLabel(pickerPeriodDateHolder.getLabel());
        PERIOD = pickerPeriodDateHolder.getLabel();
        // set set organisation unit and data set ids
//        datasetInfoHolder.setOrgUnitId(orgUnitPickerChild.getId());
        datasetInfoHolder.setOrgUnitId(OU_ID);
        datasetInfoHolder.setOrgUnitLabel(OU_NAME);
        datasetInfoHolder.setFormId(DS_ID);
        datasetInfoHolder.setFormLabel(DS_NAME);
        handleDataEntryButton(datasetInfoHolder);
    }
//    }

    private boolean areAllPrimaryPickersPresent(List<Picker> pickers) {
        return pickers != null && pickers.size() > 1 &&
                pickers.get(ORG_UNIT_PICKER_ID) != null &&
                pickers.get(ORG_UNIT_PICKER_ID).getSelectedChild() != null &&
                pickers.get(DATASET_PICKER_ID) != null &&
                pickers.get(DATASET_PICKER_ID).getSelectedChild() != null;
    }

    private boolean areAllSecondaryPickersPresent(List<Picker> pickers) {
        if (pickers == null) {
            return false;
        }

        for (Picker secondaryPicker : pickers) {
            if (secondaryPicker.getSelectedChild() == null) {
                return false;
            }
        }

        return true;
    }

    private void handleDataEntryButton(final DatasetInfoHolder info) {
        if (info != null) {
            String unit = String.format("%s: %s",
                    getString(R.string.organization_unit), info.getOrgUnitLabel());
            String period = String.format("%s: %s",
                    getString(R.string.period), info.getPeriodLabel());

            // setting labels
            formTextView.setText(DS_NAME);
            formDescriptionTextView.setText(info.getPeriodLabel());
            organisationUnitTextView.setText(OU_NAME);

            dataEntryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataEntryActivity.navigateTo(getActivity(), info);
                }
            });

            // dataEntryButton.setVisibility(View.VISIBLE);
            offlineSavedIcon.setVisibility(View.GONE);
            showOfflineSaved(info);
            currentDataSetInfoHolder = info;
            if (!dataEntryButton.isShown()) {
                perfomInAnimation(getActivity(), R.anim.in_left, dataEntryButton);
            }
        } else {
            // reset all strings
            formTextView.setText("");
            formDescriptionTextView.setText("");
            organisationUnitTextView.setText("");

            // hide button
            // dataEntryButton.setVisibility(View.GONE);
            if (dataEntryButton.isShown()) {
                perfomOutAnimation(getActivity(), R.anim.out_right, true, dataEntryButton);
            }
        }
    }

    private void showOfflineSaved(DatasetInfoHolder info) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DatasetInfoHolder.TAG, info);
        if (info != null) {
            getLoaderManager().restartLoader(SAVED_OFFLINE_LOADER_ID, bundle,
                    offlineDatasetLoader).forceLoad();
        }
    }

    private void showProgressBar() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void hideProgressBar() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void startUpdate() {
        Log.i("startUpdate()", "Starting update of dataSets");

        Context context = getActivity();
        if (context == null) {
            return;
        }

        boolean isConnectionAvailable = NetworkUtils.checkConnection(context);
        if (isConnectionAvailable) {
            showProgressBar();

            // Prepare Intent and start service
            Intent intent = new Intent(getActivity(), WorkService.class);
            intent.putExtra(WorkService.METHOD, WorkService.METHOD_UPDATE_DATASETS);
            getActivity().startService(intent);
        } else {
            String message = getString(R.string.check_connection);
            ToastManager.makeToast(context, message, Toast.LENGTH_LONG).show();
            hideProgressBar();
        }
    }

    /* this BroadcastReceiver waits for response with updates from service */
    private BroadcastReceiver onFormsUpdateListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressBar();

            int networkStatusCode = intent.getExtras().getInt(Response.CODE);
            int parsingStatusCode = intent.getExtras().getInt(JsonHandler.PARSING_STATUS_CODE);

            if (HTTPClient.isError(networkStatusCode)) {
                String message = HTTPClient.getErrorMessage(getActivity(), networkStatusCode);
                showError(message, getActivity());
            }

            if (parsingStatusCode != JsonHandler.PARSING_OK_CODE) {
                String message = getString(R.string.bad_response);
                showError(message, getActivity());
            }

            loadData();
        }
    };

    private void showError(String message, FragmentActivity activity) {
        DialogHandler dialogHandler = new DialogHandler(activity, message);
        dialogHandler.showMessage();
    }

    public static Activity getActiveActivity() {
        return activity;
    }

    /* This class is responsible for async. data loading from storage */
    private static class DataLoader extends AsyncTaskLoader<Picker> {

        public DataLoader(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public Picker loadInBackground() {
            String jSourceUnits;
            if (TextFileUtils.doesFileExist(getContext(), TextFileUtils.Directory.ROOT,
                    TextFileUtils.FileNames.ORG_UNITS_WITH_DATASETS)) {
                jSourceUnits = TextFileUtils.readTextFile(getContext(),
                        TextFileUtils.Directory.ROOT,
                        TextFileUtils.FileNames.ORG_UNITS_WITH_DATASETS);
            } else {
                return null;
            }

            ArrayList<OrganizationUnit> units = null;
            try {
                JsonArray jUnits = JsonHandler.buildJsonArray(jSourceUnits);
                Type type = new TypeToken<ArrayList<OrganizationUnit>>() {
                    // capturing type
                }.getType();
                units = JsonHandler.fromJson(jUnits, type);
            } catch (ParsingException e) {
                e.printStackTrace();
            }

            String chooseOrganisationUnit = getContext().getString(R.string.choose_unit);
            String chooseDataSet = getContext().getString(R.string.choose_data_set);
            String choose = getContext().getString(R.string.choose);
            Picker rootNode = null;

            if (units != null && !units.isEmpty()) {
                rootNode = new Picker.Builder()
                        .hint("mat chine")
                        .build();
                for (OrganizationUnit organisationUnit : units) {
                    Picker organisationUnitPicker = new Picker.Builder()
                            .id(organisationUnit.getId())
                            .name(organisationUnit.getLabel())
                            .hint(organisationUnit.getLabel())
                            .build();
                    OU_NAME = organisationUnit.getLabel();
                    OU_ID = organisationUnit.getId();
                    rootNode = organisationUnitPicker;
                    if (organisationUnit.getForms() == null ||
                            organisationUnit.getForms().isEmpty()) {
                        continue;
                    }

                    // going through data set
                    for (Form dataSet : organisationUnit.getForms()) {
                        FormOptions formOptions = null;
                        if (dataSet.getOptions() != null) {
                            // we need to pull out options from dataset and set them as tag
                            formOptions = dataSet.getOptions();
                        }

                        Picker dataSetPicker = new Picker.Builder()
                                .id(dataSet.getId())
                                .name(dataSet.getLabel())
                                .parent(organisationUnitPicker)
                                .tag(formOptions)
                                .build();
                        DS_NAME = dataSet.getLabel();
                        DS_ID = dataSet.getId();
//                        organisationUnitPicker.addChild(dataSetPicker);

                        if (dataSet.getCategoryCombo() == null ||
                                dataSet.getCategoryCombo().getCategories() == null) {
                            continue;
                        }

                        for (Category category : dataSet.getCategoryCombo().getCategories()) {
                            String label = String.format(Locale.getDefault(), "%s %s",
                                    choose, category.getLabel());
                            Picker categoryPicker = new Picker.Builder()
                                    .id(category.getId())
                                    .hint(label)
                                    .parent(dataSetPicker)
                                    .asPseudoRoot()
                                    .build();
                            dataSetPicker.addChild(categoryPicker);

                            if (category.getCategoryOptions() == null ||
                                    category.getCategoryOptions().isEmpty()) {
                                continue;
                            }

                            for (CategoryOption option : category.getCategoryOptions()) {
                                Picker categoryOptionPicker = new Picker.Builder()
                                        .id(option.getId())
                                        .name(option.getLabel())
                                        .parent(categoryPicker)
                                        .tag(option)
                                        .build();

                                // building filters
                                OrganisationUnitsFilter organisationUnitsFilter =
                                        new OrganisationUnitsFilter(option.getOrganisationUnits());

                                // we need to parse dates which are located within option
                                DateTime startDate = null;
                                DateTime endDate = null;

                                if (!TextUtils.isEmpty(option.getStartDate())) {
                                    startDate = DateTime.parse(option.getStartDate());
                                }

                                if (!TextUtils.isEmpty(option.getEndDate())) {
                                    endDate = DateTime.parse(option.getEndDate());
                                }

                                Filter periodFilter = PeriodFilterFactory.getPeriodFilter(startDate,
                                        endDate, dataSet.getOptions().getPeriodType());

                                // adding filters which will be triggered in PickerItemAdapter
                                categoryOptionPicker.addFilter(organisationUnitsFilter);
                                categoryOptionPicker.addFilter(periodFilter);

                                categoryPicker.addChild(categoryOptionPicker);
                            }
                        }

                    }
                    if (DS_ID.equals("gQVY1wTuiIU")){
                        rootNode.addChild(organisationUnitPicker);
                    }
                    else
                    {
                        tv_no_dataSets.setVisibility(View.VISIBLE);
                    }

                }
            }

            return rootNode;
        }
    }

    private static class OrganisationUnitsFilter implements Filter, Serializable {
        private final List<String> organisationUnitIds;
        private String organisationUnitId;

        OrganisationUnitsFilter(List<String> organisationUnitIds) {
            this.organisationUnitIds = organisationUnitIds;
        }

        void setOrganisationUnitId(String organisationUnitId) {
            this.organisationUnitId = organisationUnitId;
        }

        @Override
        public boolean apply() {
            if (organisationUnitIds == null || organisationUnitIds.isEmpty()) {
                return false;
            }

            if (organisationUnitId == null) {
                return false;
            }

            return !organisationUnitIds.contains(organisationUnitId);
        }
    }

    private static class OfflineDataSetLoader extends AsyncTaskLoader<Boolean> {
        private final DatasetInfoHolder infoHolder;

        public OfflineDataSetLoader(Context context, DatasetInfoHolder infoHolder) {
            super(context);
            this.infoHolder = infoHolder;
        }

        @Override
        public Boolean loadInBackground() {
            if (infoHolder.getFormId() != null && TextFileUtils.doesFileExist(
                    getContext(), TextFileUtils.Directory.DATASETS, infoHolder.getFormId())) {
                Log.d("infoHolder-------",infoHolder.getFormId());
                // try to fit values
                // from storage into form
                return isOfflineDataset();
            }
            return false;
        }

        private boolean isOfflineDataset() {

            String reportKey = DatasetInfoHolder.buildKey(infoHolder);
            Log.d("reportKey-------",reportKey);
            if (isEmpty(reportKey)) {
                return false;
            }

            return reportExists(reportKey);

        }

        private boolean reportExists(String reportKey) {
            if (isEmpty(reportKey)) {
                return false;
            }
            //@Sou 26-07 load from new offline report
            if (TextFileUtils.doesFileExist(
                    getContext(), TextFileUtils.Directory.OFFLINE_DATASETS_, reportKey)) {
                return true;
            }
            return false;
        }
    }

    private LoaderManager.LoaderCallbacks<Picker> pickerLoader =
            new LoaderManager.LoaderCallbacks<Picker>() {


                @Override
                public Loader<Picker> onCreateLoader(int id, Bundle args) {
                    return new DataLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<Picker> loader, Picker data) {
                    pickerAdapterOne.swapData(data);
                }

                @Override
                public void onLoaderReset(Loader<Picker> loader) {
                    periodPickerLinearLayout.setVisibility(View.GONE);
                    hideSpinners();
                    hideProgressBar();
                    setupViews(rootView, savedInstanceState);
                }
            };

    private LoaderManager.LoaderCallbacks<Boolean> offlineDatasetLoader =
            new LoaderManager.LoaderCallbacks<Boolean>() {


                @Override
                public Loader<Boolean> onCreateLoader(int id, Bundle args) {
                    DatasetInfoHolder datasetInfoHolder = args.getParcelable(DatasetInfoHolder.TAG);
                    return new OfflineDataSetLoader(getContext(), datasetInfoHolder);
                }

                @Override
                public void onLoadFinished(Loader<Boolean> loader, Boolean hasOfflineDataSet) {
                    if (hasOfflineDataSet) {
                        offlineSavedIcon.setVisibility(View.VISIBLE);
                        offlineSavedIcon.setImageResource(R.drawable.ic_offline);
                    } else {
                        offlineSavedIcon.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoaderReset(Loader<Boolean> loader) {

                }
            };

    private class DatasetSentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DatasetInfoHolder datasetInfoHolder = intent.getExtras().getParcelable(
                    DatasetInfoHolder.TAG);
            if (currentDataSetInfoHolder != null && datasetInfoHolder.getFormId().equals(
                    currentDataSetInfoHolder.getFormId())) {

                //ToDO @Sou fix Toast messages
                ToastManager.makeToast(AggregateReportFragment.getActiveActivity(), AggregateReportFragment.getActiveActivity().getString(R.string.offline_msg), Toast.LENGTH_SHORT).show();
                if (getActivity()!=null && isNetworkAvailable())
              {
                  pgsBar.setVisibility(View.VISIBLE);
                  tv_pb.setVisibility(View.VISIBLE);
                  AggregateReportFragment.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                          WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
              }

                offlineSavedIcon.setVisibility(View.VISIBLE);
                if (intent.getAction().equals(SAVED_ONLINE_ACTION)) {
                    offlineSavedIcon.setImageResource(R.drawable.ic_from_server);
                    AggregateReportFragment.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    pgsBar.setVisibility(View.GONE);
                    tv_pb.setVisibility(View.GONE);
                    ToastManager.makeToast(AggregateReportFragment.getActiveActivity(), PERIOD + " " + AggregateReportFragment.getActiveActivity().getString(R.string.online_msg), Toast.LENGTH_LONG).show();
                } else if (intent.getAction().equals(SAVED_OFFLINE_ACTION)) {
                    offlineSavedIcon.setImageResource(R.drawable.ic_offline);

                }


            }

        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
