import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { UnknownTournament } from '../../models/tournament.model';
import { FormBuilder, Validators } from '@angular/forms';
import {AuthService} from "../../services/auth.service";
import {TournamentService} from "../../services/tournament.service";
import {CourtService} from "../../services/court.service";
import {filter, take} from "rxjs/operators";
import {Court} from "../../models/court.model";
import {User} from "../../models/user.model";
import {BehaviorSubject} from "rxjs";

enum TournamentFormKey {
  Start = 'Start',
  End = 'End',
  Court = 'Court',
  Name = 'Name',
  Capacity = 'Capacity',
  Prize = 'Prize',
}

@Component({
  selector: 'tc-tournament-form',
  templateUrl: './tournament-form.component.html',
  styleUrls: ['./tournament-form.component.scss'],
})
export class TournamentFormComponent implements OnInit {
  @Output() readonly cancelClick = new EventEmitter<void>();
  @Output() readonly tournamentChange = new EventEmitter<UnknownTournament>();
  @Output() readonly addUser = new EventEmitter<void>();
  @Output() readonly withdrawUser = new EventEmitter<void>();

  @Input()
  set tournament(tournament: UnknownTournament) {
    this.tournamentForm.setValue({
      [TournamentFormKey.Start]: tournament.startTime,
      [TournamentFormKey.End]: tournament.endTime,
      [TournamentFormKey.Court]: tournament.court.id,
      [TournamentFormKey.Name]: tournament.name,
      [TournamentFormKey.Capacity]: tournament.capacity,
      [TournamentFormKey.Prize]: tournament.prize,
    });
  }

  @Input() readOnly = false;
  @Input() isEnrolledAlready$ = new BehaviorSubject<boolean>(false);
  @Input() submitButtonText = 'Submit';
  @Input() cancelButtonText = 'Cancel';

  readonly courts$ = this.courtService.orderedCourts$;

  readonly TournamentFormKey = TournamentFormKey;
  readonly currentTime = new Date();

  readonly tournamentForm = this.fb.group({
    [TournamentFormKey.Start]: ['', Validators.required],
    [TournamentFormKey.End]: ['', Validators.required],
    [TournamentFormKey.Court]: [undefined, Validators.required],
    [TournamentFormKey.Name]: ['', Validators.required],
    [TournamentFormKey.Capacity]: '',
    [TournamentFormKey.Prize]: ['', Validators.required],
  });

  constructor(private readonly fb: FormBuilder,
              private readonly authService: AuthService,
              private readonly tournamentService: TournamentService,
              private readonly courtService: CourtService) {}

  ngOnInit(): void {
    this.courtService.getCourts();
  }

  submit(): void {
    const { value } = this.tournamentForm;

    this.courtService.singleCourt$(value[TournamentFormKey.Court])
        .pipe(take(1), filter((court):court is Court => court != null))
        .subscribe(court => {
            const tournament: UnknownTournament = {
              type: EventType.Tournament,
              startTime: value[TournamentFormKey.Start],
              endTime: value[TournamentFormKey.End],
              court,
              name: value[TournamentFormKey.Name],
              capacity: value[TournamentFormKey.Capacity],
              prize: value[TournamentFormKey.Prize],
            };

            this.tournamentForm.markAsPristine();
            this.tournamentChange.emit(tournament);

        });
  }

  addPlayer(): void {
    this.addUser.emit();
  }

  withdrawPlayer(): void {
    this.withdrawUser.emit();
  }

  cancel(): void {
    this.cancelClick.emit();
  }
}
