package cn.onism.chatglm.model;

import lombok.Data;

import java.util.List;

/**
 * 图像响应结果
 *
 * @author HeXin
 * @date 2024/03/27
 */
@Data
public class ImageResponse {
    /**
     * 请求创建时间，是以秒为单位的Unix时间戳。
     */
    private Long created;

    /**
     * 数据
     */
    private List<Image> data;

    /**
     * 图像
     *
     * @author HeXin
     * @date 2024/03/27
     */
    @Data
    public static class Image{
        /**
         * 图像 url
         */
        private String url;
    }
}
