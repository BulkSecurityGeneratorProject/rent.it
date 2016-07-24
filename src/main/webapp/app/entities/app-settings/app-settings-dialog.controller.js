(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('AppSettingsDialogController', AppSettingsDialogController);

    AppSettingsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'AppSettings'];

    function AppSettingsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, AppSettings) {
        var vm = this;

        vm.appSettings = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.appSettings.id !== null) {
                AppSettings.update(vm.appSettings, onSaveSuccess, onSaveError);
            } else {
                AppSettings.save(vm.appSettings, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rentitApp:appSettingsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
