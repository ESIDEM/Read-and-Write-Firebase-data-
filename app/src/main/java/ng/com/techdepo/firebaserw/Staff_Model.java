package ng.com.techdepo.firebaserw;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by ESIDEM jnr on 2/1/2017.
 */

public class Staff_Model {

    private String staffName;
    private String staffAge;
    private String staffLevel;

    public Staff_Model(){}


    public Staff_Model(String staffName,String staffAge,  String staffLevel){
        this.staffName = staffName;
        this.staffAge = staffAge;
        this.staffLevel = staffLevel;
    }



    public String getStaffAge() {
        return staffAge;
    }

    public void setStaffAge(String staffAge) {
        this.staffAge = staffAge;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }


    public String getStaffLevel() {
        return staffLevel;
    }

    public void setStaffLevel(String staffLevel) {
        this.staffLevel = staffLevel;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("staffName", staffName);
        result.put("staffAge", staffAge);
        result.put("staffLevel", staffLevel);

        return result;
    }
}
