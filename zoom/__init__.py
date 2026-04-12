# All instructions: file index equals task index
from ..base import AppTasks, TaskItem

from .eval_1 import verify_host_personal_meeting
from .eval_2 import verify_invite_amber_to_new_meeting
from .eval_3 import verify_copy_invite_link_then_leave
from .eval_4 import verify_screen_share_in_new_meeting
from .eval_5 import verify_safe_driving_hello
from .eval_6 import verify_media_enabled_before_host
from .eval_7 import verify_send_im_lcl_in_new_meeting
from .eval_8 import verify_busy_status
from .eval_9 import verify_display_name_updated
from .eval_10 import verify_join_994488281_with_camera
from .eval_11 import verify_unmute_all_389257198
from .eval_12 import verify_raise_lower_hand_with_thumbs_up
from .eval_13 import verify_contact_count_answer
from .eval_14 import verify_upcoming_schedule_count_answer
from .eval_15 import verify_schedule_tomorrow_1900_with_derek_and_brittany
from .eval_16 import verify_daily_1000_with_all_contacts
from .eval_17 import verify_cancel_may_first_schedule
from .eval_18 import verify_message_natalie_about_next_monday_leave
from .eval_19 import verify_delay_tomorrow_noon_to_1300
from .eval_20 import verify_enable_waiting_room_and_extend_noon_meeting

ZOOM_TASKS = AppTasks(
    package_name="com.example.zoom",
    task_items=[
        TaskItem(
            instruction="使用个人会议 ID 发起会议，关闭本地麦克风、打开摄像头，复制邀请链接，在会议聊天中发送“Welcome to [GUIA-01]”，然后离开会议但不结束会议。",
            verify_func=verify_host_personal_meeting,
            human_steps=9,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="创建一场名为“[GUIA-02] Instant Sync”的即时会议，邀请 Amber Campbell 和 Brittany Evans，开启等候室，关闭“允许参会者在主持人前加入”，复制会议号和邀请链接后结束会议。",
            verify_func=verify_invite_amber_to_new_meeting,
            human_steps=11,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="加入会议 994488281；如果本地麦克风已开启则将其关闭，只保留摄像头开启；进入后发送“I'm lcl.”，举手后放下手，再回复一个点赞 emoji，然后离开会议。",
            verify_func=verify_copy_invite_link_then_leave,
            human_steps=11,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="以主持人身份加入会议 389257198，打开参与者列表，先静音所有人，再取消 Amber Campbell 的静音，在聊天中发送“Amber, please start.”，然后锁定会议。",
            verify_func=verify_screen_share_in_new_meeting,
            human_steps=11,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="创建一场即时会议并开始屏幕共享，暂停共享后在聊天中发送“Sharing paused.”，恢复共享后停止共享，并结束会议。",
            verify_func=verify_safe_driving_hello,
            human_steps=10,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="创建一场即时会议，先打开麦克风和摄像头，再关闭音频连接，向聊天发送“Audio disconnected, video on.”，然后结束会议。",
            verify_func=verify_media_enabled_before_host,
            human_steps=9,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="预约一场明天 19:00–20:30 的会议，标题设为“[GUIA-07] Project Sync”，邀请 Derek Stewart 和 Brittany Evans，开启等候室和密码，打开主持人视频、关闭参会者视频，保存后返回会议列表。",
            verify_func=verify_send_im_lcl_in_new_meeting,
            human_steps=10,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="预约一场后天 09:30–10:00 的重复会议，标题设为“[GUIA-08] Daily Standup”，设置为每个工作日重复，邀请 Amber Campbell 和 Natalie Cox，关闭“允许参会者在主持人前加入”，保存。",
            verify_func=verify_busy_status,
            human_steps=10,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="找到标题为“[GUIA-07] Project Sync”的会议，将开始时间改为明天 19:30，时长改为 2 小时，增加 Natalie Cox 为参会人，并开启等候室。",
            verify_func=verify_display_name_updated,
            human_steps=8,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="找到明天 12:00 的预约会议；如果存在，就把它改到 13:00 并把时长延长到 4 小时；如果不存在，就新建一场明天 13:00–17:00 的会议并邀请 Amber Campbell。",
            verify_func=verify_join_994488281_with_camera,
            human_steps=9,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="打开明天 08:00 的预约会议，复制邀请链接，然后分别给 Derek Stewart 和 Brittany Evans 发送消息“会议链接已更新，请查收。”并粘贴该链接。",
            verify_func=verify_unmute_all_389257198,
            human_steps=12,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="取消所有标题包含“[GUIA]”且日期为 5 月 1 日的预约会议中最早开始的一场，并返回列表确认它已经不存在。",
            verify_func=verify_raise_lower_hand_with_thumbs_up,
            human_steps=6,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="统计当前所有“未开始”的预约会议数量，然后打开最早开始的一场，复制其邀请链接并返回会议列表。",
            verify_func=verify_contact_count_answer,
            human_steps=7,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="在联系人列表中搜索 Natalie Cox，发送消息“下周一的会议我需要请假”，返回聊天列表并确认这条会话出现在最近聊天中。",
            verify_func=verify_upcoming_schedule_count_answer,
            human_steps=6,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="在联系人列表中找到 Amber Campbell 和 Derek Stewart，分别发送“Please confirm tomorrow's meeting.”，然后统计当前未读聊天会话的数量。",
            verify_func=verify_schedule_tomorrow_1900_with_derek_and_brittany,
            human_steps=7,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="将个人状态改为“忙碌”，再把显示名称改成“Liu Chenlong”，返回主页确认新状态和新名称已经生效。",
            verify_func=verify_daily_1000_with_all_contacts,
            human_steps=7,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="打开设置页面，关闭“进入会议时自动连接音频”，打开“进入会议时自动开启摄像头”，然后立即创建一场会议，确认摄像头已开启且音频未连接。",
            verify_func=verify_cancel_may_first_schedule,
            human_steps=8,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="使用个人会议 ID 发起会议，复制邀请链接后给 Amber Campbell 发送“请使用这条链接加入会议：”并粘贴邀请链接，然后返回会议并结束会议。",
            verify_func=verify_message_natalie_about_next_monday_leave,
            human_steps=9,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="查找未来 7 天内所有未开始的会议，统计数量，并把其中开始时间最晚的一场标题改为“[GUIA-19] Final Review”。",
            verify_func=verify_delay_tomorrow_noon_to_1300,
            human_steps=8,
            is_reasoning=True,
        ),
        TaskItem(
            instruction="找到最近一场由你主持且尚未开始的会议，开启等候室，关闭“允许参会者在主持人前加入”，保存后返回会议列表。",
            verify_func=verify_enable_waiting_room_and_extend_noon_meeting,
            human_steps=7,
            is_reasoning=False,
        ),
    ],
)
