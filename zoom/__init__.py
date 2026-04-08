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
from .eval_21 import verify_start_tomorrow_0800_meeting
from .eval_22 import verify_message_in_tomorrow_0800_meeting
from .eval_23 import verify_invite_all_contacts_to_tomorrow_0800_meeting

ZOOM_TASKS = AppTasks(
    package_name="com.example.zoom",
    task_items=[
        TaskItem(
            instruction='使用个人会议 ID 来创建一场会议',
            verify_func=verify_host_personal_meeting,
            human_steps=4,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='创建一场会议，并邀请联系人 Amber Campbell 进入会议',
            verify_func=verify_invite_amber_to_new_meeting,
            human_steps=6,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='创建一场会议，并复制会议邀请链接，然后暂离会议',
            verify_func=verify_copy_invite_link_then_leave,
            human_steps=6,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='创建一场会议，并开启屏幕共享',
            verify_func=verify_screen_share_in_new_meeting,
            human_steps=5,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='创建一场会议，并进入安全驾驶模式，说一句“Hello”后退出会议',
            verify_func=verify_safe_driving_hello,
            human_steps=7,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='打开麦克风和摄像头，关闭音频，创建会议',
            verify_func=verify_media_enabled_before_host,
            human_steps=6,
            is_reasoning=False,
        ),
        TaskItem(
            instruction="在新创建的会议中，发送消息“I'm lcl.”",
            verify_func=verify_send_im_lcl_in_new_meeting,
            human_steps=5,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='进入个人设置页面，将我修改成忙碌状态',
            verify_func=verify_busy_status,
            human_steps=4,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='把我的名字修改为“Liu Chenlong”',
            verify_func=verify_display_name_updated,
            human_steps=4,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='加入会议 994488281 ，同时打开我的摄像头',
            verify_func=verify_join_994488281_with_camera,
            human_steps=5,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='在会议 389257198 中取消所有人的静音',
            verify_func=verify_unmute_all_389257198,
            human_steps=6,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='在会议 927650367 中举手回答问题，回答问题后放下手并回复“点赞”的 emoji',
            verify_func=verify_raise_lower_hand_with_thumbs_up,
            human_steps=7,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='数一下我总共有几个联系人',
            verify_func=verify_contact_count_answer,
            human_steps=2,
            is_reasoning=True,
        ),
        TaskItem(
            instruction='数一下现在有几个预约会议还没开始',
            verify_func=verify_upcoming_schedule_count_answer,
            human_steps=2,
            is_reasoning=True,
        ),
        TaskItem(
            instruction='预约一场明天晚上19：00的会议，邀请Derek Stewart和Brittany Evans参加',
            verify_func=verify_schedule_tomorrow_1900_with_derek_and_brittany,
            human_steps=7,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='每天早上十点固定预约邀请所有联系人来开早会',
            verify_func=verify_daily_1000_with_all_contacts,
            human_steps=7,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='取消 5 月 1 日的预约会议',
            verify_func=verify_cancel_may_first_schedule,
            human_steps=4,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='给 Natalie Cox 发消息说下周一的会议我需要请个假',
            verify_func=verify_message_natalie_about_next_monday_leave,
            human_steps=4,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='把已经预约的明天中午12：00的会议推迟到明天下午一点',
            verify_func=verify_delay_tomorrow_noon_to_1300,
            human_steps=6,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='打开明天中午12：00预约会议的等候室，并将会议时长延长到4小时',
            verify_func=verify_enable_waiting_room_and_extend_noon_meeting,
            human_steps=6,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='提前开始明天8：00预约的会议',
            verify_func=verify_start_tomorrow_0800_meeting,
            human_steps=4,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='在预约的明天8：00会议中发消息，说我可能迟到十分钟',
            verify_func=verify_message_in_tomorrow_0800_meeting,
            human_steps=5,
            is_reasoning=False,
        ),
        TaskItem(
            instruction='邀请我的所有联系人来参加明天8：00预约的会议',
            verify_func=verify_invite_all_contacts_to_tomorrow_0800_meeting,
            human_steps=6,
            is_reasoning=False,
        ),
    ],
)
