<template>
  <v-data-table
    :headers="headers"
    :items="weeklyScores"
    :items-per-page="15"
    :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
  >
    <template v-slot:top>
      <v-card-title>
        <h1 class="table-tile">Weekly Scores</h1>
        <v-spacer />
        <v-btn
          color="primary"
          dark
          data-cy="weeklyScoresRefreshButton"
          @click="updateWeeklyScores()"
          >Refresh
        </v-btn>
      </v-card-title>
    </template>

    <template v-slot:[`item.actions`]="{ item }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-icon
            class="mr-2 action-button"
            v-on="on"
            data-cy="weeklyScoresDeleteButton"
            @click="deleteWeeklyScore(item.id)"
            color="red"
            >delete</v-icon
          >
        </template>
        <span>Delete WeeklyScore</span>
      </v-tooltip>
    </template>

    <template v-slot:[`item.week`]="{ item }">
      <span> {{ item.week }} </span>
    </template>

    <template v-slot:[`item.numberAnswered`]="{ item }">
      <span> {{ item.numberAnswered }} </span>
    </template>

    <template v-slot:[`item.percentageCorrect`]="{ item }">
      <span> {{ item.percentageCorrect }} </span>
    </template>
  </v-data-table>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { ISOtoString } from '@/services/ConvertDateService';
import WeeklyScore from '@/models/dashboard/WeeklyScore';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class WeeklyScoresView extends Vue {
  @Prop({ type: Number, required: true }) readonly dashboardId!: number;

  weeklyScores: WeeklyScore[] = [];

  headers: object = [
    {
      text: 'Actions',
      value: 'actions',
      align: 'center',
      sortable: false,
      width: '20%',
    },
    {
      text: 'Week',
      value: 'week',
      align: 'center',
      width: '20%',
    },
    {
      text: 'Number Answered',
      value: 'numberAnswered',
      align: 'center',
      width: '20%',
    },
    {
      text: 'Uniquely Answered',
      value: 'uniquelyAnswered',
      align: 'center',
      width: '20%',
    },
    {
      text: 'Percentage Correct',
      value: 'percentageCorrect',
      align: 'center',
      width: '20%',
    },
  ];

  async created() {
    await this.getWeeklyScoresList(this.dashboardId);
  }

  async getWeeklyScoresList(dashboardId: number) {
    await this.$store.dispatch('loading');
    try {
      this.weeklyScores = await RemoteServices.getWeeklyScores(dashboardId);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async deleteWeeklyScore(weeklyScoreId: number) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.removeWeeklyScore(this.dashboardId, weeklyScoreId);
      this.weeklyScores = await RemoteServices.getWeeklyScores(
        this.dashboardId
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async updateWeeklyScores() {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.updateWeeklyScores(this.dashboardId);
      this.weeklyScores = await RemoteServices.getWeeklyScores(
        this.dashboardId!
      );
      this.$emit('refresh', ISOtoString(Date()));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.table-tile {
  font-size: 28px;
  font-weight: bold;
  letter-spacing: 2px;
}
</style>
