package cn.onism.chatglm.model;

import cn.hutool.json.JSONUtil;
import cn.onism.chatglm.constant.Model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 图像大模型请求参数<br>
 * CogView <a href="https://open.bigmodel.cn/dev/api#cogview">根据用户的文字描述生成图像,使用同步调用方式请求接口</a>
 *
 * @author HeXin
 * @date 2024/03/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageRequest {
    /**
     * 模型；24年1月发布了 cogview-3 生成图片模型
     */
    private Model model = Model.COGVIEW_3;

    /**
     * 所需图像的文本描述
     */
    private String prompt;

    public String getModel() {
        return model.value();
    }

    public Model getModelEnum() {
        return model;
    }

    @Override
    public String toString() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("model", model.value());
        paramsMap.put("prompt", prompt);
        return JSONUtil.toJsonStr(paramsMap);
    }
}
