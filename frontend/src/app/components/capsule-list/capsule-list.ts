import {Component, inject, OnInit} from '@angular/core';
import {CapsuleListCard} from './capsule-list-card/capsule-list-card';
import {CapsuleService} from '../../service/capsule.service';

@Component({
  selector: 'app-capsule-list',
  imports: [
    CapsuleListCard
  ],
  templateUrl: './capsule-list.html',
  styleUrl: './capsule-list.scss',
})
export class CapsuleList implements OnInit {
  private capsuleService = inject(CapsuleService);
  capsuleResumes = this.capsuleService.capsuleResumes;

  ngOnInit() {
    this.capsuleService.loadCapsules();
  }
}
