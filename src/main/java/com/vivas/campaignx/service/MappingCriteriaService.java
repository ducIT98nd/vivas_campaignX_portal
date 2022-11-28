package com.vivas.campaignx.service;

import com.vivas.campaignx.entity.MappingCriteria;
import com.vivas.campaignx.repository.MappingCriteriaRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MappingCriteriaService {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    @Autowired
    private MappingCriteriaRepository repository;
    @Autowired
    private PackageDataService packageDataService;

    public Optional<MappingCriteria> getByIdTargetGroup(Long idTargetGroup) {
        return repository.getByIdTargetGroup(idTargetGroup);
    }

    public List<MappingCriteria> findAllByIdTargetGroup(Long idTargetGroup) {
        return repository.findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(idTargetGroup);
    }

    public List<MappingCriteria> findAllByIdSubTargetGroup(Long idSubTargetGroup) {
        return repository.findAllByIdSubTargetGroup(idSubTargetGroup);
    }

    public String detailCriteria(String jsonNode){
        StringBuilder htmlField = new StringBuilder();
        JSONObject jsonObject = new JSONObject(jsonNode);
        int criteriaID = jsonObject.getInt("criteriaId");
        if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 65 || criteriaID == 66) {
            htmlField.append(jsonObject.getString("name"));
            String operator = jsonObject.getString("operator");
            if(operator.equals("=")) htmlField.append(" bằng ");
            else if(operator.equals("!=")) htmlField.append(" không bằng ");
            else if(operator.equals("<")) htmlField.append(" nhỏ hơn ");
            else if(operator.equals(">")) htmlField.append(" lớn hơn ");
            else if(operator.equals("<=")) htmlField.append(" nhỏ hơn hoặc bằng ");
            else if(operator.equals(">=")) htmlField.append(" lớn hơn hoặc bằng ");
            else if(operator.equals("><")) htmlField.append(" trong khoảng từ ");
            String value = jsonObject.getString("value");
            String unit = jsonObject.getString("unit");
            htmlField.append(value);
            htmlField.append(" ");
            htmlField.append(unit);

            if(operator.equals("><")) {
                htmlField.append(" đến ");
                String valueEnd = jsonObject.getString("valueEnd");
                htmlField.append(valueEnd);
                htmlField.append(" ");
                htmlField.append(unit);
            }

        } else if (criteriaID == 3 || criteriaID == 4) {
            if (criteriaID == 3) {
                htmlField.append(jsonObject.getString("name"));
                String[] arrValue = jsonObject.getString("value").split(";");
                for(int i = 0; i < arrValue.length; i++) {
                    String valueTmp = arrValue[i];
                    if(valueTmp.equals("recall")) htmlField.append(" Thu hồi số;");
                    else if(valueTmp.equals("lock2")) htmlField.append(" Khóa 2 chiều;");
                    else if(valueTmp.equals("lock1")) htmlField.append(" Khóa 1 chiều;");
                    else if(valueTmp.equals("active")) htmlField.append(" Hoạt động;");
                }
            } else if (criteriaID == 4) {
                htmlField.append(jsonObject.getString("name"));
                String value = jsonObject.getString("value");
                if(value.equals("PREPAID")) htmlField.append(" trả trước ");
                else if(value.equals("POSTPAID")) htmlField.append(" trả sau ");
            }
        } else if (criteriaID == 5) {
            htmlField.append(jsonObject.getString("name"));
            String operator = jsonObject.getString("operator");
            if(operator.equals("is")) htmlField.append(" là ");
            else if(operator.equals("isnot")) htmlField.append(" không là ");
            String value = jsonObject.getString("value");
            htmlField.append(value);
        } else if (criteriaID == 56) {
          //  htmlField += jsonObject.getString("name");
            htmlField.append(" Gói DATA ");
            String operator = jsonObject.getString("operator");
            if(operator.equals("using")) htmlField.append(" đang sử dụng ");
            else if(operator.equals("notInUse")) htmlField.append(" đang không sử dụng ");
            String value = jsonObject.getString("value");
            if(value.equals("all")) htmlField.append(":Tất cả ");
            else{
                String serviceName = packageDataService.findById(Long.parseLong(value)).get().getPackageName();
                htmlField.append(serviceName);
            }

        } else if (criteriaID == 60) {
         //   htmlField += jsonObject.getString("name");
            htmlField.append(" Gói KMCB ");
            String operator = jsonObject.getString("operator");
            String serviceCode = jsonObject.getString("serviceCode");
            if(operator.equals("using")) htmlField.append(" đang sử dụng ");
            else if(operator.equals("notInUse")) htmlField.append(" đang không sử dụng ");
            String value = jsonObject.getString("value");
            if(value.equals("all")) htmlField.append(" :Tất cả ");
            else{
                String serviceName = packageDataService.findById(Long.parseLong(value)).get().getPackageName();
                htmlField.append(serviceName);
            }
        } else if(criteriaID == 58 || criteriaID == 57){
            htmlField.append(jsonObject.getString("name"));
            String operator = jsonObject.getString("operator");
            if(operator.equals("=")) htmlField.append(" bằng ");
            else if(operator.equals("!=")) htmlField.append(" không bằng ");
            else if(operator.equals("<")) htmlField.append(" nhỏ hơn ");
            else if(operator.equals(">")) htmlField.append(" lớn hơn ");
            else if(operator.equals("<=")) htmlField.append(" nhỏ hơn hoặc bằng ");
            else if(operator.equals(">=")) htmlField.append(" lớn hơn hoặc bằng ");
            else if(operator.equals("><")) htmlField.append(" trong khoảng từ ");

            String value = jsonObject.getString("value");
            String unit = jsonObject.getString("unit");
            String serviceCode = jsonObject.getString("serviceCode");
            String serviceName = packageDataService.findById(Long.parseLong(serviceCode)).get().getPackageName();
            htmlField.append(value);
            htmlField.append(" ");
            htmlField.append(unit);

            if(operator.equals("><")) {
                htmlField.append(" đến ");
                String valueEnd = jsonObject.getString("valueEnd");
                htmlField.append(valueEnd);
                htmlField.append(" ");
                htmlField.append(unit);
            }
            htmlField.append(" gói");
            htmlField.append(" ");
            htmlField.append(serviceName);

        } else if(criteriaID == 62 || criteriaID == 61){
            htmlField.append(jsonObject.getString("name"));
            String operator = jsonObject.getString("operator");
            if(operator.equals("=")) htmlField.append(" bằng ");
            else if(operator.equals("!=")) htmlField.append(" không bằng ");
            else if(operator.equals("<")) htmlField.append(" nhỏ hơn ");
            else if(operator.equals(">")) htmlField.append(" lớn hơn ");
            else if(operator.equals("<=")) htmlField.append(" nhỏ hơn hoặc bằng ");
            else if(operator.equals(">=")) htmlField.append(" lớn hơn hoặc bằng ");
            else if(operator.equals("><")) htmlField.append(" trong khoảng từ ");

            String value = jsonObject.getString("value");
            String unit = jsonObject.getString("unit");
            String serviceCode = jsonObject.getString("serviceCode");
            String serviceName = packageDataService.findById(Long.parseLong(serviceCode)).get().getPackageName();
            htmlField.append(value);
            htmlField.append(" ");
            htmlField.append(unit);

            if(operator.equals("><")) {
                htmlField.append(" đến ");
                String valueEnd = jsonObject.getString("valueEnd");
                htmlField.append(valueEnd);
                htmlField.append(" ");
                htmlField.append(unit);
            }
            htmlField.append(" gói");
            htmlField.append(" ");
            htmlField.append(serviceName);
        }else {
            htmlField.append(jsonObject.getString("name"));
            String operator = jsonObject.getString("operator");
            if(operator.equals("=")) htmlField.append(" bằng ");
            else if(operator.equals("!=")) htmlField.append(" không bằng ");
            else if(operator.equals("<")) htmlField.append(" nhỏ hơn ");
            else if(operator.equals(">")) htmlField.append(" lớn hơn ");
            else if(operator.equals("<=")) htmlField.append(" nhỏ hơn hoặc bằng ");
            else if(operator.equals(">=")) htmlField.append(" lớn hơn hoặc bằng ");
            else if(operator.equals("><")) htmlField.append(" trong khoảng từ ");

            String value = jsonObject.getString("value");
            String unit = jsonObject.getString("unit");
            htmlField.append(value);
            htmlField.append(" ");
            htmlField.append(unit);
            if(operator.equals("><")) {
                htmlField.append(" đến ");
                String valueEnd = jsonObject.getString("valueEnd");
                htmlField.append(valueEnd);
                htmlField.append(" ");
                htmlField.append(unit);
            }
            htmlField.append(" ");
            String operatorTime = jsonObject.getString("operatorTime");
            if(operatorTime.equals("1")) htmlField.append("Trong 60 ngày gần nhất");
            else if(operatorTime.equals("2")) htmlField.append("Trong 30 ngày gần nhất");
            else if(operatorTime.equals("3")) htmlField.append("Trong 7 ngày gần nhất");
            else if(operatorTime.equals("4")) htmlField.append("Trong tháng hiện tại");
            else if(operatorTime.equals("5")) htmlField.append("Trong tháng trước");
            else if(operatorTime.equals("6")) htmlField.append("Trong khoảng thời gian");
            if(operatorTime.equals("6")) htmlField.append(" từ ngày ").append(jsonObject.getString("startTime")).append(" đến ngày ").append(jsonObject.getString("endTime"));
        }
        return htmlField.toString();
    }

    public void deleteAllByIdSubTargetGroup(Long idTargetGroup){
        repository.deleteAllByIdSubTargetGroup(idTargetGroup);
    }
}
