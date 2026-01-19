import {Component, input} from '@angular/core';
import {CapsuleResume} from '../../../modele/capsuleResume';
import {RouterLink} from '@angular/router';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-capsule-list-card',
  imports: [
    RouterLink,
    DatePipe
  ],
  templateUrl: './capsule-list-card.html',
  styleUrl: './capsule-list-card.scss',
})
export class CapsuleListCard {
  capsuleResume = input.required<CapsuleResume>();
}
