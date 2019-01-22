package cn.dagongniu.bitman.account.view;

import cn.dagongniu.bitman.base.IView;

public interface IFileUpdoadView extends IView {

    void setFullFaceContainerSuccess(String data);

    void setReverseFaceContainerSuccess(String data);

    void setFullFaceContainerfailure(String data);

    void setReverseContainerfailure(String data);

}
