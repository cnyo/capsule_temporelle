import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {CapsuleForm} from './capsule-form/capsule-form';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CapsuleForm],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('TimeCapsule');
}
