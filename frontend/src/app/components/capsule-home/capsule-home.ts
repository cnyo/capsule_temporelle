import {Component, inject} from '@angular/core';
import {CapsuleForm} from "../capsule-form/capsule-form";
import {CapsuleList} from "../capsule-list/capsule-list";
import {CapsuleService} from '../../service/capsule.service';
import {CapsuleFormValue} from '../../modele/capsuleFormValue';

@Component({
  selector: 'app-capsule-home',
    imports: [
        CapsuleForm,
        CapsuleList
    ],
  templateUrl: './capsule-home.html',
  styleUrl: './capsule-home.scss',
})
export class CapsuleHome {
  private capsuleService = inject(CapsuleService);
  requestStatus = this.capsuleService.status;

  protected onSubmitForm(capsuleFormValue: CapsuleFormValue) {
    this.capsuleService.createCapsule(capsuleFormValue);
  }
}
