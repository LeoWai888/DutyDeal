package com.gd.icbc.dutydeal.contract;

import com.gd.icbc.dutydeal.base.BasePresenter;
import com.gd.icbc.dutydeal.base.BaseView;
import com.gd.icbc.dutydeal.json.DutyingPeople;
import com.gd.icbc.dutydeal.json.ParamObj;
import com.gd.icbc.dutydeal.json.SignPeople;

import java.util.List;

public interface HomeContract {

    interface View extends BaseView<Presenter> {
        void showDutyingPeople(List<DutyingPeople.ResDataBean> resData);
        void showSignPeople(SignPeople SignPeopleData);
        String getPhotosData(byte[] image);
        void initParams(ParamObj params);
        void showError(String msg);
        void showInitError(String msg);
    }

    interface Presenter extends BasePresenter {
        void loadDutyingPeoples(String areaNo);
        void loadSignPeople(String faceBase64,String areaNo);
        void loadParam();
    }
}