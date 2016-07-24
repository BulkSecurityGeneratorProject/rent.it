'use strict';

describe('Controller Tests', function() {

    describe('AppSettings Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockAppSettings;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockAppSettings = jasmine.createSpy('MockAppSettings');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'AppSettings': MockAppSettings
            };
            createController = function() {
                $injector.get('$controller')("AppSettingsDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'rentitApp:appSettingsUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
