import {Component, output} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {CapsuleFormValue} from '../../modele/capsuleFormValue';

@Component({
  selector: 'app-capsule-form',
  imports: [
    FormsModule
  ],
  templateUrl: './capsule-form.html',
  styleUrl: './capsule-form.scss',
})
export class CapsuleForm {
  formSubmitted = output<CapsuleFormValue>();
  message: string = "";
  launchDate: string = "";

  onSubmitForm(form: NgForm): void {
    this.formSubmitted.emit(form.value as CapsuleFormValue);
  }
}
