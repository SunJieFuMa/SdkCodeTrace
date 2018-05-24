package test.sunzeping.com.testauthor;

/**
 * Created by sunjie on 2018/5/19.
 */

public class CallbackData {

    /**
     * extraMsg : {"sign":"T7+Yre+8hOTWEeJqu3oNczgBDfENpuu39NBvKAd9ExHYJsBDb9lr6CI8XDxrOr9BdmGTvyw328Nu
     dn0Nl3uAFskNX70dsIlDpLkSqC4/pr0CnxrOwqQkfViYHvJ2olJjoxROxXyQtHtXwAOWqYs1ELut
     tX10U2fiGdpzfy8e5QI=
     ","tradeNum":"ad0eb84339d808045ecc1d8adf79c7ed91f40c36ea9df504","tradeTime":"d9668085c69c2ecb70eaffb179c17dcbee1cf9f2caef8946","amount":"e5a6c3761ab9ddaf","currency":"ac7132c57f10d3ce","status":"e00c693e6c5b8a60","note":"dd762e2dc91f21fb9398efd83243b1f6"}
     * payStatus : JDP_PAY_SUCCESS
     */

    private String extraMsg;
    private String payStatus;

    public String getExtraMsg() {
        return extraMsg;
    }

    public void setExtraMsg(String extraMsg) {
        this.extraMsg = extraMsg;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }
}
