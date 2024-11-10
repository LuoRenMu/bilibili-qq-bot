package cn.luorenmu.action.listenProcess.entity.response

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.11.03 16:17
 */


data class BilibiliSpace(
    val code: Long,
    val message: String,
    val ttl: Long,
    val data: Data,
)

data class Data(
    @JSONField(name = "has_more")
    val hasMore: Boolean,
    val items: List<Item>,
    val offset: String,
    @JSONField(name = "update_baseline")
    val updateBaseline: String,
    @JSONField(name = "update_num")
    val updateNum: Long,
)

data class Item(
    val basic: Basic,
    @JSONField(name = "id_str")
    val idStr: String,
    val modules: Modules,
    val type: String,
    val visible: Boolean,
)

data class Basic(
    @JSONField(name = "comment_id_str")
    val commentIdStr: String,
    @JSONField(name = "comment_type")
    val commentType: Long,
    @JSONField(name = "like_icon")
    val likeIcon: LikeIcon,
    @JSONField(name = "rid_str")
    val ridStr: String,
)

data class LikeIcon(
    @JSONField(name = "action_url")
    val actionUrl: String,
    @JSONField(name = "end_url")
    val endUrl: String,
    val id: Long,
    @JSONField(name = "start_url")
    val startUrl: String,
)

data class Modules(
    @JSONField(name = "module_author")
    val moduleAuthor: ModuleAuthor,
    @JSONField(name = "module_dynamic")
    val moduleDynamic: ModuleDynamic,
    @JSONField(name = "module_more")
    val moduleMore: ModuleMore,
    @JSONField(name = "module_stat")
    val moduleStat: ModuleStat,

    //确定其是否为置顶消息
    @JSONField(name = "module_tag")
    val moduleTag: ModuleTag?,
)

data class ModuleAuthor(
    val avatar: Avatar,
    val face: String,
    @JSONField(name = "face_nft")
    val faceNft: Boolean,
    val following: Any?,
    @JSONField(name = "jump_url")
    val jumpUrl: String,
    val label: String,
    val mid: Long,
    val name: String,
    @JSONField(name = "official_verify")
    val officialVerify: OfficialVerify,
    val pendant: Pendant,
    @JSONField(name = "pub_action")
    val pubAction: String,
    @JSONField(name = "pub_location_text")
    val pubLocationText: String,
    @JSONField(name = "pub_time")
    val pubTime: String,
    @JSONField(name = "pub_ts")
    val pubTs: Long,
    val type: String,
    val vip: Vip,
)

data class Avatar(
    @JSONField(name = "container_size")
    val containerSize: ContainerSize,
    @JSONField(name = "fallback_layers")
    val fallbackLayers: FallbackLayers,
    val mid: String,
)

data class ContainerSize(
    val height: Double,
    val width: Double,
)

data class FallbackLayers(
    @JSONField(name = "is_critical_group")
    val isCriticalGroup: Boolean,
    val layers: List<Layer>,
)

data class Layer(
    @JSONField(name = "general_spec")
    val generalSpec: GeneralSpec,
    @JSONField(name = "layer_config")
    val layerConfig: LayerConfig,
    val resource: Resource,
    val visible: Boolean,
)

data class GeneralSpec(
    @JSONField(name = "pos_spec")
    val posSpec: PosSpec,
    @JSONField(name = "render_spec")
    val renderSpec: RenderSpec,
    @JSONField(name = "size_spec")
    val sizeSpec: SizeSpec,
)

data class PosSpec(
    @JSONField(name = "axis_x")
    val axisX: Double,
    @JSONField(name = "axis_y")
    val axisY: Double,
    @JSONField(name = "coordinate_pos")
    val coordinatePos: Long,
)

data class RenderSpec(
    val opacity: Long,
)

data class SizeSpec(
    val height: Double,
    val width: Double,
)

data class LayerConfig(
    @JSONField(name = "is_critical")
    val isCritical: Boolean?,
    val tags: Tags,
)

data class Tags(
    @JSONField(name = "AVATAR_LAYER")
    val avatarLayer: Map<String, Any>?,
    @JSONField(name = "GENERAL_CFG")
    val generalCfg: GeneralCfg?,
    @JSONField(name = "PENDENT_LAYER")
    val pendentLayer: Map<String, Any>?,
    @JSONField(name = "ICON_LAYER")
    val iconLayer: Map<String, Any>?,
)

data class GeneralCfg(
    @JSONField(name = "config_type")
    val configType: Long,
    @JSONField(name = "general_config")
    val generalConfig: GeneralConfig,
)

data class GeneralConfig(
    @JSONField(name = "web_css_style")
    val webCssStyle: WebCssStyle,
)

data class WebCssStyle(
    @JSONField(name = "background-color")
    val backgroundColor: String?,
    val border: String?,
    val borderRadius: String,
    val boxSizing: String?,
)

data class Resource(
    @JSONField(name = "res_image")
    val resImage: ResImage?,
    @JSONField(name = "res_type")
    val resType: Long,
)

data class ResImage(
    @JSONField(name = "image_src")
    val imageSrc: ImageSrc,
)

data class ImageSrc(
    val placeholder: Long?,
    val remote: Remote?,
    @JSONField(name = "src_type")
    val srcType: Long,
    val local: Long?,
)

data class Remote(
    @JSONField(name = "bfs_style")
    val bfsStyle: String,
    val url: String,
)

data class OfficialVerify(
    val desc: String,
    val type: Long,
)

data class Pendant(
    val expire: Long,
    val image: String,
    @JSONField(name = "image_enhance")
    val imageEnhance: String,
    @JSONField(name = "image_enhance_frame")
    val imageEnhanceFrame: String,
    @JSONField(name = "n_pid")
    val nPid: Long,
    val name: String,
    val pid: Long,
)

data class Vip(
    @JSONField(name = "avatar_subscript")
    val avatarSubscript: Long,
    @JSONField(name = "avatar_subscript_url")
    val avatarSubscriptUrl: String,
    @JSONField(name = "due_date")
    val dueDate: Long,
    val label: Label,
    @JSONField(name = "nickname_color")
    val nicknameColor: String,
    val status: Long,
    @JSONField(name = "theme_type")
    val themeType: Long,
    val type: Long,
)

data class Label(
    @JSONField(name = "bg_color")
    val bgColor: String,
    @JSONField(name = "bg_style")
    val bgStyle: Long,
    @JSONField(name = "border_color")
    val borderColor: String,
    @JSONField(name = "img_label_uri_hans")
    val imgLabelUriHans: String,
    @JSONField(name = "img_label_uri_hans_static")
    val imgLabelUriHansStatic: String,
    @JSONField(name = "img_label_uri_hant")
    val imgLabelUriHant: String,
    @JSONField(name = "img_label_uri_hant_static")
    val imgLabelUriHantStatic: String,
    @JSONField(name = "label_theme")
    val labelTheme: String,
    val path: String,
    val text: String,
    @JSONField(name = "text_color")
    val textColor: String,
    @JSONField(name = "use_img_label")
    val useImgLabel: Boolean,
)

data class ModuleDynamic(
    val additional: Additional?,
    val desc: Desc?,
    val major: Major?,
    val topic: Topic?,
)

data class Additional(
    val common: Common,
    val type: String,
)

data class Common(
    val button: Button,
    val cover: String,
    val desc1: String,
    val desc2: String,
    @JSONField(name = "head_text")
    val headText: String,
    @JSONField(name = "id_str")
    val idStr: String,
    @JSONField(name = "jump_url")
    val jumpUrl: String,
    val style: Long,
    @JSONField(name = "sub_type")
    val subType: String,
    val title: String,
)

data class Button(
    @JSONField(name = "jump_style")
    val jumpStyle: JumpStyle,
    @JSONField(name = "jump_url")
    val jumpUrl: String,
    val type: Long,
)

data class JumpStyle(
    @JSONField(name = "icon_url")
    val iconUrl: String,
    val text: String,
)

data class Desc(
    @JSONField(name = "rich_text_nodes")
    val richTextNodes: List<RichTextNode>,
    val text: String,
)

data class RichTextNode(
    @JSONField(name = "orig_text")
    val origText: String,
    val text: String,
    val type: String,
    @JSONField(name = "jump_url")
    val jumpUrl: String?,
    val style: Any?,
)

data class Major(
    val draw: Draw?,
    val type: String,
    val article: Article?,
    val archive: Archive?,
    val opus: Opus?,
)

data class Opus(
    @JSONField(name = "jump_url")
    val jumpUrl: String,
)

data class Draw(
    val id: Long,
    val items: List<Item2>,
)

data class Item2(
    val height: Long,
    val size: Double,
    val src: String,
    val tags: List<Any?>,
    val width: Long,
)

data class Article(
    val covers: List<String>,
    val desc: String,
    val id: Long,
    @JSONField(name = "jump_url")
    val jumpUrl: String,
    val label: String,
    val title: String,
)

data class Archive(
    val aid: String,
    val badge: Badge,
    val bvid: String,
    val cover: String,
    val desc: String,
    @JSONField(name = "disable_preview")
    val disablePreview: Long,
    @JSONField(name = "duration_text")
    val durationText: String,
    @JSONField(name = "jump_url")
    val jumpUrl: String,
    val stat: Stat,
    val title: String,
    val type: Long,
)

data class Badge(
    @JSONField(name = "bg_color")
    val bgColor: String,
    val color: String,
    @JSONField(name = "icon_url")
    val iconUrl: Any?,
    val text: String,
)

data class Stat(
    val danmaku: String,
    val play: String,
)

data class Topic(
    val id: Long,
    @JSONField(name = "jump_url")
    val jumpUrl: String,
    val name: String,
)

data class ModuleMore(
    @JSONField(name = "three_point_items")
    val threePointItems: List<ThreePointItem>,
)

data class ThreePointItem(
    val label: String,
    val type: String,
)

data class ModuleStat(
    val comment: Comment,
    val forward: Forward,
    val like: Like,
)

data class Comment(
    val count: Long,
    val forbidden: Boolean,
)

data class Forward(
    val count: Long,
    val forbidden: Boolean,
)

data class Like(
    val count: Long,
    val forbidden: Boolean,
    val status: Boolean,
)

data class ModuleTag(
    val text: String,
)
