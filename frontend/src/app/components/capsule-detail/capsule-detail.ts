import {Component, inject, OnInit, Signal} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {CapsuleService} from '../../service/capsule.service';
import {toSignal} from '@angular/core/rxjs-interop';
import {Capsule} from '../../modele/capsule';
import {filter, map, switchMap, tap} from 'rxjs';

@Component({
  selector: 'app-capsule-detail',
  imports: [
    RouterLink
  ],
  templateUrl: './capsule-detail.html',
  styleUrl: './capsule-detail.scss',
})
export class CapsuleDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private capsuleService = inject(CapsuleService);
  capsule: Signal<Capsule | null> = toSignal(
    this.route.paramMap.pipe(
      map(params => params.get('id')),
      filter((id): id is string => !!id),
      switchMap(id => this.capsuleService.getCapsuleById(id))
    ), { initialValue: null });

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      throw new Error('Id is missing');
    }
  }
}
