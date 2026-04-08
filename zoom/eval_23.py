from ._shared import evaluate_task


def verify_invite_all_contacts_to_tomorrow_0800_meeting(
    result=None,
    device_id=None,
    backup_dir=None,
    **kwargs,
) -> bool:
    return evaluate_task(
        task_id=23,
        result=result,
        device_id=device_id,
        backup_dir=backup_dir,
    )


if __name__ == "__main__":
    print(verify_invite_all_contacts_to_tomorrow_0800_meeting())
