'use strict';

describe('Controller Tests', function() {

    describe('Product Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProduct, MockUser, MockProductAddress, MockComment, MockCategory, MockTag;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProduct = jasmine.createSpy('MockProduct');
            MockUser = jasmine.createSpy('MockUser');
            MockProductAddress = jasmine.createSpy('MockProductAddress');
            MockComment = jasmine.createSpy('MockComment');
            MockCategory = jasmine.createSpy('MockCategory');
            MockTag = jasmine.createSpy('MockTag');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Product': MockProduct,
                'User': MockUser,
                'ProductAddress': MockProductAddress,
                'Comment': MockComment,
                'Category': MockCategory,
                'Tag': MockTag
            };
            createController = function() {
                $injector.get('$controller')("ProductDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'rentitApp:productUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
