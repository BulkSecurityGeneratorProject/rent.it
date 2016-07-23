(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('CommentDialogController', CommentDialogController);

    CommentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Comment'];

    function CommentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Comment) {
        var vm = this;

        vm.comment = entity;
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
            if (vm.comment.id !== null) {
                Comment.update(vm.comment, onSaveSuccess, onSaveError);
            } else {
                Comment.save(vm.comment, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rentitApp:commentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
