<template>
  <v-container fluid>
    <v-data-table
      :headers="headers"
      :items="failedAnswers"
      :sort-by="['collected']"
      sort-desc
      multi-sort
      :items-per-page="10"
      :footer-props="{ itemsPerPageOptions: [10, 25, 50, 75, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <h1 class="table-tile">Failed Answers</h1>
          <v-spacer />
          <v-btn
            color="primary"
            dark
            data-cy="failedAnswersRefreshButton"
            @click="updateFailedAnswers()"
            >Refresh
          </v-btn>
        </v-card-title>
      </template>
      <template v-slot:[`item.questionContent`]="{ item }">
        <span> {{ item.questionContent }} </span>
      </template>

      <template v-slot:[`item.answered`]="{ item }">
        <span> {{ item.answered }} </span>
      </template>

      <template v-slot:[`item.collected`]="{ item }">
        <span> {{ item.collected }} </span>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              data-cy="showStudentViewDialogButton"
              @click="showStudentViewDialog(item)"
              >school</v-icon
            >
          </template>
          <span>Student View</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              data-cy="failedAnswersDeleteButton"
              @click="deleteFailedAnswer(item.id)"
              color="red"
              >delete</v-icon
            >
          </template>
          <span>Delete FailedAnswer</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <student-view-dialog
      v-if="statementQuestion && studentViewDialog"
      v-model="studentViewDialog"
      :statementQuestion="statementQuestion"
      v-on:close-show-question-dialog="onCloseStudentViewDialog"
    />
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { ISOtoString } from '@/services/ConvertDateService';
import FailedAnswer from '@/models/dashboard/FailedAnswer';
import RemoteServices from '@/services/RemoteServices';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StudentViewDialog from '@/views/student/dashboard/StudentViewDialog.vue';

@Component({
  components: {
    'student-view-dialog': StudentViewDialog,
  },
})
export default class failedAnswersView extends Vue {
  @Prop({ type: Number, required: true }) readonly dashboardId!: number;

  failedAnswers: FailedAnswer[] = [];
  statementQuestion: StatementQuestion | null = null;
  studentViewDialog: boolean = false;
  headers: object = [
    {
      text: 'Actions',
      value: 'actions',
      align: 'center',
      sortable: false,
      width: '20%',
    },
    {
      text: 'Question',
      value: 'questionContent',
      align: 'center',
      width: '40%',
    },
    {
      text: 'Answered',
      value: 'answered',
      align: 'center',
      width: '20%',
    },
    {
      text: 'Collected',
      value: 'collected',
      align: 'center',
      width: '20%',
    },
  ];

  async created() {
    await this.getFailedAnswersList(this.dashboardId);
  }

  async getFailedAnswersList(dashboardId: number) {
    await this.$store.dispatch('loading');
    try {
      this.failedAnswers = await RemoteServices.getFailedAnswers(dashboardId);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
  async updateFailedAnswers() {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.updateFailedAnswers(this.dashboardId);
      this.failedAnswers = await RemoteServices.getFailedAnswers(
        this.dashboardId!
      );
      this.$emit('refresh', ISOtoString(Date()));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
  async deleteFailedAnswer(failedAnswerId: number) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.removeFailedAnswer(failedAnswerId);
      this.failedAnswers = await RemoteServices.getFailedAnswers(
        this.dashboardId
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
  //testing
  async showStudentViewDialog(failedAnswer: FailedAnswer) {
    try {
      this.statementQuestion = await RemoteServices.getStatementQuestion(
        failedAnswer.questionId
      );
      this.studentViewDialog = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
  onCloseStudentViewDialog() {
    this.statementQuestion = null;
    this.studentViewDialog = false;
  }
}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;
  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>
